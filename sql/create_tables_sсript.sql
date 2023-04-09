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

CREATE OR REPLACE FUNCTION auto_update_for_last_update()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
    AS
$$
BEGIN
    NEW.last_update_date = NOW();
    RETURN NEW;
END;
$$;

CREATE TRIGGER set_timestamp_in_case_of_update
    BEFORE UPDATE OF id, name, description, price, duration
    ON gift_certificate
    FOR EACH ROW
EXECUTE PROCEDURE auto_update_for_last_update();

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



INSERT INTO gift_certificate VALUES (3, 'DIVING', 'DESCRIPTION', 100.6, 10);
INSERT INTO gift_certificate VALUES (4, 'DRIVING', 'DESCRIPTION', 11.4, 13);


SELECT id, name FROM tag
    INNER JOIN gift_certificates_tags gct on tag.id = gct.tag_id
WHERE certificate_id=31;