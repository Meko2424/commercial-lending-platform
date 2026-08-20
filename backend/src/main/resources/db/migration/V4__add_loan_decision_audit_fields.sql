ALTER TABLE loan_applications
    ADD COLUMN reviewed_by UUID,
    ADD COLUMN reviewed_at TIMESTAMP,
    ADD COLUMN decision_reason VARCHAR(1000)