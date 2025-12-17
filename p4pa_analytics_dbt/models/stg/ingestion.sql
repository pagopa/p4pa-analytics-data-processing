{{ config(
    materialized = "incremental",
    unique_key = "ingestion_pk",
    incremental_strategy = "merge",
    )
}}

--text extraction of json fields--
with source as (
    select
    {{ dbt_utils.generate_surrogate_key(['i.ingestion_pk']) }} as ingestion_pk,
    (i.ingestion_payload ->> 'eventId')::varchar(256) as event_id,
    (i.ingestion_payload -> 'payload' ->> 'status')::varchar(256) as status,
    (i.ingestion_payload -> 'payload' ->> 'fileSize')::bigint as file_size,
    (i.ingestion_payload -> 'payload' ->> 'totalRows')::bigint as total_rows,
    (i.ingestion_payload -> 'payload' ->> 'processedRows')::bigint as processed_rows,
    (i.ingestion_payload -> 'payload' ->> 'organizationId')::bigint as organization_id,
	  -- MAPPING WORKFLOW TYPE --
    case split_part((i.ingestion_payload ->> 'eventDescription'), ' ', 1)
        when 'DebtPositionIngestionFlowWFImpl' then 'DP_INSTALLMENTS'
        when 'ReceiptPagopaIngestionWFImpl' then 'RECEIPT_PAGOPA'
        when 'ReceiptIngestionWFImpl' then 'RECEIPT'
        when 'PaymentsReportingIngestionWFImpl' then 'PAYMENTS_REPORTING_PAGOPA'
        when 'PaymentsReportingIngestionWFImpl' then 'PAYMENTS_REPORTING'
        when 'TreasuryOpiIngestionWFImpl' then 'TREASURY_OPI'
        when 'TreasuryCsvIngestionWFImpl' then 'TREASURY_CSV'
        when 'TreasuryPosteIngestionWFImpl' then 'TREASURY_POSTE'
        when 'TreasuryXlsIngestionWFImpl' then 'TREASURY_XLS'
        when 'TreasuryCsvCompleteIngestionWFImpl' then 'TREASURY_CSV_COMPLETE'
        when 'PaymentNotificationIngestionWFImpl' then 'PAYMENT_NOTIFICATION'
        when 'SendNotificationIngestionFlowWFImpl' then 'SEND_NOTIFICATION'
        when 'OrganizationIngestionWFImpl' then 'ORGANIZATIONS'
        when 'OrgSilServiceIngestionWFImpl' then 'ORGANIZATIONS_SIL_SERVICE'
        when 'DebtPositionTypeIngestionWFImpl' then 'DEBT_POSITIONS_TYPE'
        when 'DebtPositionTypeOrgIngestionWFImpl' then 'DEBT_POSITIONS_TYPE_ORG'
        when 'REPLACE_CON_CASO_Operatori_abilitati' then 'DEBT_POSITIONS_TYPE_ORG_OPERATORS'
        when 'AssessmentsIngestionWFImpl' then 'ASSESSMENTS'
        when 'AssessmentsRegistryIngestionWFImpl' then 'ASSESSMENTS_REGISTRY'
        else split_part((i.ingestion_payload ->> 'eventDescription'), ' ', 1)
    end::varchar(255) as ingestion_flow_file_type,
    (i.ingestion_payload -> 'payload' ->> 'ingestionFlowFileId')::bigint as ingestion_flow_file_id,
    (i.ingestion_payload -> 'payload' ->> 'operatorExternalUserId')::varchar(255) as operator_external_user_id,
    (i.ingestion_payload ->> 'traceId')::varchar(255) as trace_id,
    (i.ingestion_payload ->> 'eventType')::varchar(255) as event_type,
    (i.ingestion_payload ->> 'eventDateTime')::timestamp  as event_date_time,
    (i.ingestion_payload ->> 'eventDescription')::text as event_description,
    current_timestamp as target_processed_time

    from raw.ingestion i

    {% if is_incremental() %}
        where i.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
)



select
  ingestion_pk,
  event_id,
  status,
  file_size,
  total_rows,
  processed_rows,
  organization_id,
  ingestion_flow_file_type,
  ingestion_flow_file_id,
  operator_external_user_id,
  event_type,
  event_date_time,
  event_description,
  -- technical fields
  target_processed_time as processed_time
from source
