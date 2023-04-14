SET TIMEZONE TO 'Europe/Kiev';


CREATE TABLE gift_certificate(
    id SERIAL,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    price DECIMAL NOT NULL,
    duration INTEGER NOT NULL,
    create_date TIMESTAMP DEFAULT now(),
    last_update_date TIMESTAMP DEFAULT now(),
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE tag(
    id SERIAL,
    name VARCHAR NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE gift_certificates_tags(
    certificate_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (certificate_id, tag_id),
    FOREIGN KEY(certificate_id)
        REFERENCES gift_certificate(id)
        ON DELETE CASCADE,
    FOREIGN KEY(tag_id)
        REFERENCES tag(id)
        ON DELETE CASCADE
);


-- This trigger blocks the ability to update 'create_date' field
CREATE OR REPLACE FUNCTION stop_change_for_create_date()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    NEW.create_date := OLD.create_date;
    RETURN OLD;
END;
$$;
CREATE TRIGGER prevent_create_date_changes
    BEFORE UPDATE OF create_date
    ON gift_certificate
    FOR EACH ROW
EXECUTE PROCEDURE stop_change_for_create_date();


-- This function and trigger are updating 'last_update_date' field in 'gift_certificate' table
-- after any of fields in 'gift_certificate' table was updated.
CREATE OR REPLACE FUNCTION update_gift_certificate_last_update_date()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    UPDATE gift_certificate SET last_update_date = NOW() WHERE id = NEW.id;
    RETURN NEW;
END;
$$;
CREATE TRIGGER update_gift_certificate_last_update_date_on_certificate_update
    AFTER UPDATE OF id, name, description, price, duration
    ON gift_certificate
    FOR EACH ROW
EXECUTE PROCEDURE update_gift_certificate_last_update_date();


-- This function and trigger are updating 'last_update_date' field in 'gift_certificate' table
-- after deleting or updating any row of 'gift_certificates_tags' table.
CREATE OR REPLACE FUNCTION update_gift_certificate_last_update_date_tags_manipulation()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF TG_OP = 'DELETE' THEN
        UPDATE gift_certificate SET last_update_date = NOW() WHERE id = OLD.certificate_id;
    ELSE
        UPDATE gift_certificate SET last_update_date = NOW() WHERE id = NEW.certificate_id;
    END IF;
    RETURN NEW;
END;
$$;
CREATE TRIGGER update_gift_certificate_last_update_date_on_tag_insert_delete
    AFTER INSERT OR DELETE
    ON gift_certificates_tags
    FOR EACH ROW
EXECUTE PROCEDURE update_gift_certificate_last_update_date_tags_manipulation();
