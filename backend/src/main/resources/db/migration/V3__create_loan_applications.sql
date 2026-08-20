CREATE TABLE loan_applications (
                                   id UUID PRIMARY KEY,
                                   business_name VARCHAR(255) NOT NULL,
                                   requested_amount NUMERIC(15, 2) NOT NULL,
                                   purpose VARCHAR(500) NOT NULL,
                                   status VARCHAR(50) NOT NULL,
                                   created_by UUID NOT NULL,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_loan_application_created_by
                                       FOREIGN KEY (created_by)
                                           REFERENCES employees(id)
);