ALTER TABLE technical_log_entries ADD loggable_id INT NOT NULL;
ALTER TABLE technical_log_entries ADD CONSTRAINT fk_technical_log_entries_loggable_id__id FOREIGN KEY (loggable_id) REFERENCES loggable(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE technical_log_entries DROP COLUMN loggable;
