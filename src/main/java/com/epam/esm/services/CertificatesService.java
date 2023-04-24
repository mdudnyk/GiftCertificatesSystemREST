package com.epam.esm.services;

import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;

import java.util.List;

/**
 * @author Myroslav Dudnyk
 */

public interface CertificatesService {
    CertificateDTOResp getById(int id);

    List<CertificateDTOResp> getCertificates(String tagName,
                                             String searchText,
                                             String sortBy,
                                             String sortOrder);

    CertificateDTOResp create(CertificateDTOReq certificateDTOReq);

    CertificateDTOResp update(int id, CertificateDTOReq certificateDTOReq);

    void deleteById(int id);
}
