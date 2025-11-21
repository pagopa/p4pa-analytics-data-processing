{{ config(
    materialized = "incremental",
    unique_key = "debt_position_type_org_pk",
    incremental_strategy = "merge",
    ) 
}}

-- select the row group that has not been processed yet
with source as (
    select
        {{ dbt_utils.generate_surrogate_key(['debt_position_type_org_id']) }} as debt_position_type_org_pk,
        dpto.debt_position_type_org_id,
        dpto.debt_position_type_id,
        dpto.organization_id,
        dpto.balance,
        dpto.code,
        dpto.description,
        dpto.iban,
        dpto.postal_iban,
        dpto.postal_account_code,
        dpto.holder_postal_cc,
        dpto.org_sector,
        dpto.xsd_definition_ref,
        dpto.amount_cents,
        dpto.external_payment_url,
        dpto.flag_anonymous_fiscal_code,
        dpto.flag_mandatory_due_date,
        dpto.flag_spontaneous,
        dpto.flag_notify_io,
        dpto.service_id,
        dpto.io_template_subject,
        dpto.io_template_message,
        dpto.flag_active,
        dpto.flag_notify_outcome_push,
        dpto.notify_outcome_push_org_sil_service_id,
        dpto.flag_amount_actualization,
        dpto.amount_actualization_org_sil_service_id,
        dpto.flag_external,
        dpto.spontaneous_form_id,
        dpto.creation_date,
        dpto.update_date,
        dpto.update_operator_external_id,
        dpto.update_trace_id,
        -- technical fields
        dpto.processed_time as src_processed_time,
        current_timestamp as target_processed_time
    from raw.debt_position_type_orgs dpto
    
    {% if is_incremental() %}
        where dpto.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
),

-- keep only the most recent row on a same key
ranked as (
    select
        s.*,
        row_number() over (partition by s.debt_position_type_org_id order by s.src_processed_time desc) as rn
    from source s
)

select
    debt_position_type_org_pk,
    debt_position_type_org_id,
    debt_position_type_id,
    organization_id,
    balance,
    code,
    description,
    iban,
    postal_iban,
    postal_account_code,
    holder_postal_cc,
    org_sector,
    xsd_definition_ref,
    amount_cents,
    external_payment_url,
    flag_anonymous_fiscal_code,
    flag_mandatory_due_date,
    flag_spontaneous,
    flag_notify_io,
    service_id,
    io_template_subject,
    io_template_message,
    flag_active,
    flag_notify_outcome_push,
    notify_outcome_push_org_sil_service_id,
    flag_amount_actualization,
    amount_actualization_org_sil_service_id,
    flag_external,
    spontaneous_form_id,
    creation_date,
    update_date,
    update_operator_external_id,
    update_trace_id,
    -- technical fields
    target_processed_time as processed_time
from ranked r
where rn = 1