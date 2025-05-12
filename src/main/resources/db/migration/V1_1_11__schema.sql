IF NOT EXISTS(select *
          from users
          where user_role = 'OPERATOR')
    BEGIN
        SET IDENTITY_INSERT users ON;
        INSERT INTO users (id, address, apartment_number, btag_id, bornDate, city, created_on,
                           currency, document_number, document_type, mail, first_name, language,
                           last_bonus_check, last_login, last_name, msn, neighborhood, password,
                           phone, receiveEmail, reference_point, user_role, sex, skype, state,
                           status, test_user, time_zone, twitter, user_name, zip_code, country,
                           allow_update_birthday, valid_status, trans_date, last_modification_date,
                           bo_cashier_id, billpocket_id, cellphone, vip, id_btag_netrefer,
                           receive_notifications, phone_area, phone_country, cellphone_area,
                           cellphone_country, register_language, user_plataform, cellphone_iso2,
                           player_type, hide_from_ranking, show_balance,
                           user_have_muchbetter_transaction, date_change_phone_muchbetter,
                           receive_sms, receive_call, last_update_personal_info,
                           self_exclusion_motive, agent_id)
        VALUES (1, N'AAA', N'AA', 12, N'2022-10-07 05:21:37.0000000', null,
                N'2022-10-07 05:21:46.0000000', 1, null, 1, N'AA', null, null, null,
                N'2022-10-07 05:22:40.0000000', null, null, null,
                N'f69ed068932b7578461400ca43eb8ebc7a9288e610eee9bf31c80d16fa12fe1f21f51c5b9b0144db',
                N'AA', 1, null, N'OPERATOR', null, null, null, N'ACTIVE', 0, null, null,
                N'operator', null, null, 0, 0, null, null, null, null, null, 0, null, 1, null, null,
                null, null, null, null, null, null, 0, null, null, null, null, null, null, null,
                null);
        SET IDENTITY_INSERT users OFF;
        SET IDENTITY_INSERT wallets ON;
        INSERT INTO wallets (id, balance, is_credits) VALUES (1, 0, 0);
        INSERT INTO wallets (id, balance, is_credits) VALUES (2, 100000000, 1);
        SET IDENTITY_INSERT wallets OFF;
        INSERT INTO agents
        (user_id, wallet_id, credit_wallet_id, can_have_subagents, commission, parent_agent_id,
         code_id)
        values (1, 1, 2, 1, 0, null, 1);
    END