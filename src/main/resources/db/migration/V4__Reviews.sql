--The review table contains the review of each pet for each store
--So we need store_id and pet_id and then review

CREATE TABLE REVIEWS (
    ID BIGINT PRIMARY KEY,
    PET_ID BIGINT NOT NULL REFERENCES PET(ID),
    USER_ID BIGINT NOT NULL REFERENCES USERS(ID),
    REVIEW VARCHAR NULL
);