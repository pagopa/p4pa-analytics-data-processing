{{ config(
    materialized = "incremental",
    unique_key = "assessment_detail_pk",
    incremental_strategy = "merge",
        pre_hook="
        DELETE FROM {{ this }} as t
        WHERE (t.organization_id, t.iud) IN (
            SELECT organization_id, iud
            FROM {{ ref('payment_assessment_detail_tmp') }} as tmp
            WHERE tmp.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    )
    "
    )  
    }}

--text extraction of json fields--
with source as (
    select
    {{ dbt_utils.generate_surrogate_key(['assessment_detail_id']) }} as assessment_detail_pk,
    assessment_detail_id,
    assessment_id,
    organization_id,
    debt_position_type_org_code,
    debt_position_type_org_id,
    iuv,
    iud,
    iur,
    debtor_fiscal_code_hash,
    payment_date_time,
    office_code,
    office_description,
    section_code,
    section_description,
    assessment_code,
    assessment_name,
    assessment_description,
    amount_cents,
    amount_submitted,
    receipt_id,
    classification_label,
    date_receipt,
    date_reporting,
    date_treasury,
    creation_date,
    update_date,
    update_operator_external_id,
    update_trace_id,
    processed_time as src_processed_time,
    current_timestamp as target_processed_time

    from {{ ref('payment_assessment_detail_tmp') }} tmp

    {% if is_incremental() %}
    where tmp.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
),

-- keep only the most recent row on a same key
ranked as (
    select
        s.*,
        row_number() over (partition by (s.assessment_detail_pk) order by s.src_processed_time desc) as rn
    from source s
)

select
  assessment_detail_pk,
  assessment_detail_id,
  assessment_id,
  organization_id,
  debt_position_type_org_code,
  debt_position_type_org_id,
  iuv,
  iud,
  iur,
  debtor_fiscal_code_hash,
  payment_date_time,
  office_code,
  office_description,
  section_code,
  section_description,
  assessment_code,
  assessment_name,
  assessment_description,
  amount_cents,
  amount_submitted,
  receipt_id,
  classification_label,
  date_receipt,
  date_reporting,
  date_treasury,
  creation_date,
  update_date,
  update_operator_external_id,
  update_trace_id,
  -- technical fields
  target_processed_time as processed_time
from ranked r
where rn = 1