{{ config(
    materialized = "incremental",
    unique_key = "assessment_detail_pk",
    incremental_strategy = "merge",
        pre_hook="
        UPDATE {{ this }} as t
        SET is_active = false
        WHERE (organization_id, iud) IN (
            SELECT organization_id, iud
            FROM {{ ref('payment_assessment_detail_tmp') }} as tmp
            WHERE tmp.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
        )
    "
    )  
    }}


with base as (
    select *, CAST(TO_CHAR(date_receipt, 'YYYY') AS INT) AS operating_year,
    DATE(payment_date_time) as payment_date
    from {{ ref('payment_assessment_detail') }} 
),

source as (
    select
    assessment_detail_pk,
    assessment_detail_id,
    assessment_id,
    organization_id,
    {{ dbt_utils.generate_surrogate_key(['debt_position_type_org_id']) }} as debt_position_type_org_pk,
    {{ dbt_utils.generate_surrogate_key(['office_code', 'debt_position_type_org_id', 'operating_year']) }} as office_pk,
    {{ dbt_utils.generate_surrogate_key(['section_code', 'debt_position_type_org_id', 'operating_year']) }} as section_pk,
    {{ dbt_utils.generate_surrogate_key(['assessment_code', 'debt_position_type_org_id', 'operating_year']) }} as assessment_pk,
    {{ dbt_utils.generate_surrogate_key(['payment_date']) }} as payment_date_pk,
    CASE WHEN 
    date_receipt IS NULL THEN NULL
    ELSE {{ dbt_utils.generate_surrogate_key(['date_receipt']) }} END AS date_receipt_pk,
    CASE WHEN 
    date_reporting IS NULL THEN NULL
    ELSE {{ dbt_utils.generate_surrogate_key(['date_reporting']) }} END  as date_reporting_pk,
    CASE WHEN 
    date_treasury IS NULL THEN NULL
    ELSE {{ dbt_utils.generate_surrogate_key(['date_treasury']) }} END as date_treasury_pk,
    iuv,
    iud,
    iur,
    debtor_fiscal_code_hash,
    payment_date_time,
    assessment_name,
    amount_cents,
    amount_submitted,
    receipt_id,
    classification_label,
    processed_time as src_processed_time,
    current_timestamp as target_processed_time

    from base as b

    {% if is_incremental() %}
    where b.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
)

select
    assessment_detail_pk,
    assessment_detail_id,
    assessment_id,
    organization_id,
    debt_position_type_org_pk,
    office_pk,
    section_pk,
    assessment_pk,
    payment_date_pk,
    date_receipt_pk,
    date_reporting_pk,
    date_treasury_pk,
    iuv,
    iud,
    iur,
    debtor_fiscal_code_hash,
    assessment_name,
    amount_cents,
    amount_submitted,
    receipt_id,
    classification_label,
    target_processed_time as processed_time,
    TRUE as is_active
    from source
