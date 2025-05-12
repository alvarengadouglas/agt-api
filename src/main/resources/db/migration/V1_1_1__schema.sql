INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, [scope],
                                  authorized_grant_types, web_server_redirect_uri, authorities,
                                  access_token_validity, refresh_token_validity,
                                  additional_information, autoapprove)
VALUES ('agentsmanagement-site', NULL,
        '$2y$12$ItAOhLmZX3GGFtGhNd6RBua8iJ1Tkh5n9j/5FmWakA5AWVJDthM6y',
        'READ,WRITE', 'password,refresh_token', NULL, NULL, 21600, 5184000, NULL, NULL),
       ('checktoken', NULL, '$2y$12$5waG9zz.E81yJ2ET1RgjJ.DiDqoqaRyq7J4odi.iDZ/c4GAnbD1cC', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL);