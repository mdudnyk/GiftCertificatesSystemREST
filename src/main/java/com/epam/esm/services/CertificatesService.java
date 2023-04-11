package com.epam.esm.services;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.dao.exceptions.EntityNotFoundException;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.CertificateDTO;
import com.epam.esm.models.dtos.TagDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class CertificatesService {
    private final CertificateDAO certificateDAO;
    private final TagsService tagsService;
    private final CertificatesTagsService certificatesTagsService;

    public CertificatesService(final CertificateDAO certificateDAO,
                               final TagsService tagsService,
                               final CertificatesTagsService certificatesTagsService) {
        this.certificateDAO = certificateDAO;
        this.tagsService = tagsService;
        this.certificatesTagsService = certificatesTagsService;
    }

    public List<Certificate> getAll() {
        List<Certificate> certificates = certificateDAO.getAll();

        for (Certificate certificate : certificates) {
            certificate.setTags(tagsService.getTagsByCertificateId(certificate.getId()));
        }

        return certificates;
    }

    public Certificate getById(int id) {
        Certificate certificate = certificateDAO.getById(id);
        certificate.setTags(tagsService.getTagsByCertificateId(id));

        return certificate;
    }

    @Transactional
    public Certificate create(CertificateDTO certificate) {
        int newCertificateId = certificateDAO.create(certificate);

        for (String tagName : certificate.getTags()) {
            int tagId = addTagToDBIfNotPresented(tagName);
            certificatesTagsService.create(newCertificateId, tagId);
        }

        return getById(newCertificateId);
    }

    @Transactional
    public Certificate update(int certificateId, CertificateDTO certificateNewEntity) {
        Certificate certificateOldEntity = getById(certificateId);

        if (certificateNewEntity.getTags() != null) {
            updateCertificateTags(certificateNewEntity, certificateOldEntity);
        }

        int fieldsToUpdateCount =
                fillNullFieldsOfNewEntityWithFieldsFromOldEntity(certificateNewEntity, certificateOldEntity);
        if (fieldsToUpdateCount > 0) {
            certificateDAO.update(certificateId, certificateNewEntity);
        }

        return getById(certificateId);
    }

    // This method fills the 'null' fields of the certificateNewEntity object
    // with the corresponding values from the certificateOldEntity object
    // and returns number of updated fields.
    private int fillNullFieldsOfNewEntityWithFieldsFromOldEntity(final CertificateDTO certificateNewEntity,
                                                                 final Certificate certificateOldEntity) {
        int updatedFieldCount = 0;

        String newName = certificateNewEntity.getName();
        String oldName = certificateOldEntity.getName();
        if (newName != null && !newName.equals(oldName)) {
            updatedFieldCount++;
        } else {
            certificateNewEntity.setName(oldName);
        }

        String newDescription = certificateNewEntity.getDescription();
        String oldDescription = certificateOldEntity.getDescription();
        if (newDescription != null && !newDescription.equals(oldDescription)) {
            updatedFieldCount++;
        } else {
            certificateNewEntity.setDescription(oldDescription);
        }


        BigDecimal newPrice = certificateNewEntity.getPrice();
        BigDecimal oldPrice = certificateOldEntity.getPrice();
        if (newPrice != null && newPrice.compareTo(oldPrice) != 0) {
            updatedFieldCount++;
        } else {
            certificateNewEntity.setPrice(oldPrice);
        }

        Integer newDuration = certificateNewEntity.getDuration();
        Integer oldDuration = certificateOldEntity.getDuration();
        if (newDuration != null && newDuration.compareTo(oldDuration) != 0) {
            updatedFieldCount++;
        } else {
            certificateNewEntity.setDuration(oldDuration);
        }

        return updatedFieldCount;
    }

    // Method represents business logic of handling update operation of the certificate,
    // when some tag names were passed in the update request.
    private void updateCertificateTags(final CertificateDTO certificateNewEntity, final Certificate certificateOldEntity) {
        List<Tag> tagListFromOldEntity = certificateOldEntity.getTags();
        List<String> tagNameListFromNewEntity = certificateNewEntity.getTags();
        List<String> tagNameListFromOldEntity = tagListFromOldEntity.stream().map(Tag::getName).toList();
        int certificateId = certificateOldEntity.getId();

        // This block of code checks each tag name, passed in update request,
        // for representation in old certificate entity.
        // If this not the case, a new tag will be created in the database (only if it doesn't exist in the database)
        // and added to the certificate entity.
        for (String tagName : tagNameListFromNewEntity) {
            if (isTagNameNotInTagNameList(tagName, tagNameListFromOldEntity)) {
                int tagId = addTagToDBIfNotPresented(tagName);
                certificatesTagsService
                        .create(certificateId, tagId);
            }
        }

        // This block of code checks each tag, represented in the old certificate entity,
        // for representation in the list of tag names passed in the update request.
        // If not, the absence tag will be removed from the old certificate entity.
        for (Tag tag : tagListFromOldEntity) {
            if (isTagNameNotInTagNameList(tag.getName(), tagNameListFromNewEntity)) {
                certificatesTagsService
                        .deleteByCertificateIdAndTagId(certificateId, tag.getId());
            }
        }
    }

    // Method checks if the tag name does not exist in the list of tag names.
    // If so, 'true' is returned.
    private boolean isTagNameNotInTagNameList(final String checkedTagName, final List<String> tagNameList) {
        return tagNameList.stream().filter(tagName -> tagName.equalsIgnoreCase(checkedTagName))
                .findAny()
                .isEmpty();
    }

    // Method checks whether a tag with the specified name exists in the database.
    // If so, the ID of the tag is returned.
    // If it doesn't, new tag will be created in database and return its ID.
    private int addTagToDBIfNotPresented(final String tagName) {
        Tag tmpTag;

        try {
            tmpTag = tagsService.getByName(tagName);
        } catch (EntityNotFoundException e) {
            tmpTag = tagsService.create(new TagDTO(tagName));
        }

        return tmpTag.getId();
    }

    public void deleteById(int id) {
        certificateDAO.deleteById(id);
    }
}