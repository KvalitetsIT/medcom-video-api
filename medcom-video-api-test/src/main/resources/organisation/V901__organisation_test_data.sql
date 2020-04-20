-- * organisation *
INSERT INTO organisation (id, organisation_id, name) VALUES (1, 'company 1', 'company name 1');
INSERT INTO organisation (id, organisation_id, name) VALUES (2, 'company 2', 'company name 2');
INSERT INTO organisation (id, organisation_id, name) VALUES (3, 'company 3', 'company name 3');
INSERT INTO organisation (id, organisation_id, name) VALUES (4, 'kvak', 'company name kvak');
INSERT INTO organisation (id, organisation_id, name) VALUES (5, 'test-org', 'company name test-org');
INSERT INTO organisation (id, organisation_id, name) VALUES (6, 'another-test-org', 'company name another-test-org');
INSERT INTO organisation (id, organisation_id, name, pool_size) VALUES (7, 'pool-test-org', 'company name another-test-org', 10);
INSERT INTO organisation (id, organisation_id, name, pool_size) VALUES (8, 'org-a', 'Organisationen kaldet æøå&/%', 0);
INSERT INTO organisation (id, organisation_id, name, pool_size) VALUES (9, 'org-b', 'Organisationen kaldet B', 0);
INSERT INTO organisation (id, organisation_id, name, pool_size) VALUES (10, 'sub-org-a', 'Organisationen nnder a', 0);
INSERT INTO organisation (id, organisation_id, name, pool_size) VALUES (11, 'pool-test-org1', 'Pool test org der kun findes i org service', 10);


INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (1, 1, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (2, 2, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (3, 3, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (4, 4, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (5, 5, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (6, 6, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (7, 7, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (8, 8, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (9, 9, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (10, 10, 0);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (10, 8, 1);
INSERT INTO org_hierarchy (organisation_id, parent_org_id, distance) values (11, 11, 0);