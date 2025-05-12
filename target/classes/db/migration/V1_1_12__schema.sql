IF NOT EXISTS(select *
              from users
              where user_role = 'READONLY_ADMIN')
    DECLARE @wallet_id INT;
DECLARE @credit_wallet_id INT;
DECLARE @agent_code_id INT;
DECLARE @ro_id INT;
BEGIN
    INSERT INTO users (address, apartment_number, btag_id, bornDate, city, created_on,
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
    VALUES (N'AAA', N'AA', 12, N'2022-10-07 05:21:37.0000000', null,
            N'2022-10-07 05:21:46.0000000', 1, null, 1, N'AA', null, null, null,
            N'2022-10-07 05:22:40.0000000', null, null, null,
            N'f69ed068932b7578461400ca43eb8ebc7a9288e610eee9bf31c80d16fa12fe1f21f51c5b9b0144db',
            N'AA', 1, null, N'READONLY_ADMIN', null, null, null, N'ACTIVE', 0, null, null,
            N'readonly_admin', null, null, 0, 0, null, null, null, null, null, 0, null, 1, null,
            null,
            null, null, null, null, null, null, 0, null, null, null, null, null, null, null,
            null);
    INSERT INTO wallets (balance, is_credits)
    VALUES (0, 0);
    SET @wallet_id = @@IDENTITY;

    INSERT INTO wallets (balance, is_credits)
    VALUES (0, 1);
    SET @credit_wallet_id = @@IDENTITY;

    INSERT INTO agent_codes
    (code)
    values ('roadmin');
    SET @agent_code_id = @@IDENTITY;


    INSERT INTO agents
    (user_id, wallet_id, credit_wallet_id, can_have_subagents, commission, parent_agent_id,
     code_id)
    select u.id, @wallet_id, @credit_wallet_id, 1, 0, null, @agent_code_id
    from users u
    where u.user_role = 'READONLY_ADMIN';

    SET @ro_id = @@IDENTITY;

    update agents
    set parent_agent_id = @ro_id
    from agents join users u2 on u2.id = agents.user_id
    where u2.user_role = 'OPERATOR';
END