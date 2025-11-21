{{ config(
    materialized= "incremental",
    incremental_strategy="append",
    post_hook=[
        "DELETE FROM {{ this }} WHERE processed_time < (SELECT MAX(processed_time) FROM {{ this }})
        "
    ]
    )
    }}

--text extraction of json fields--
with source as (
    select
    ac.assessment_classification_pk as assessment_detail_pk,
    (detail->>'assessmentDetailId')::bigint  as assessment_detail_id,
    (ac.assessment_classification_payload -> 'payload' ->> 'assessmentId')::bigint as assessment_id,
    (ac.assessment_classification_payload -> 'payload' ->> 'organizationId')::bigint as organization_id,
    (ac.assessment_classification_payload -> 'payload' ->> 'debtPositionTypeOrgCode')::varchar(256) as debt_position_type_org_code,
    (detail->> 'debtPositionTypeOrgId')::bigint as debt_position_type_org_id,
    (ac.assessment_classification_payload -> 'payload' ->> 'iud')::varchar(35) as iuv,
    (ac.assessment_classification_payload -> 'payload' ->> 'iur')::varchar(35) as iud,
    ac.assessment_classification_payload -> 'payload' ->> 'iuv' as iur,
    (detail->> 'debtorFiscalCodeHash')::varchar(100)  as debtor_fiscal_code_hash,
    (detail->> 'paymentDateTime')::timestamp as payment_date_time,
    (detail->> 'officeCode')::varchar(64) as office_code,
    (detail->> 'officeDescription')::varchar(512) as office_description,
    (detail->> 'sectionCode')::varchar(64) as section_code,
    (detail->> 'sectionDescription')::varchar(512) as section_description,
    (detail->> 'assessmentCode')::varchar(64) as assessment_code,
    ac.assessment_classification_payload -> 'payload' ->> 'assessmentName' as assessment_name,
    (detail->> 'assessmentDescription')::varchar(512) as assessment_description,
    (detail->> 'amountCents')::bigint as amount_cents,
    (detail->> 'amountSubmitted')::boolean as amount_submitted,
    (detail->> 'receiptId')::bigint as receipt_id,
    (detail->> 'classificationLabel')::varchar(20) as classification_label,
    (detail->> 'dateReceipt')::date as date_receipt,
    (detail->> 'dateReporting')::date as date_reporting,
    (detail->> 'dateTreasury')::date as date_treasury,
    (detail->> 'creationDate')::timestamp as creation_date,
    (detail->> 'updateDate')::timestamp as update_date,
    detail->> 'updateOperatorExternalId' as update_operator_external_id,
    detail->> 'updateTraceId' as update_trace_id,
    current_timestamp as target_processed_time

    from raw.assessments_classification ac
    CROSS JOIN LATERAL jsonb_array_elements(ac.assessment_classification_payload->'payload'->'assessmentsDetailList') AS detail

    {% if is_incremental() %}
        where ac.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
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
from source