--liquibase formatted sql

--changeset maxzhukoff:1
ALTER TABLE comment DROP CONSTRAINT comment_task_id_fkey;
ALTER TABLE comment
    ADD CONSTRAINT comment_task_id_fkey
FOREIGN KEY (task_id) REFERENCES task(task_id) ON DELETE CASCADE;
