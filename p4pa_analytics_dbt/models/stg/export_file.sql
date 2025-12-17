{{ config(
    materialized = "incremental",
    incremental_strategy = "append",
    )
}}

--text extraction of json fields--
with source as (
    select
    {{ dbt_utils.generate_surrogate_key(['ex.export_file_pk']) }} as export_file_pk,
    (ex.export_file_payload ->> 'eventId')::varchar(256) as event_id,
    (ex.export_file_payload -> 'payload' ->> 'fileSize')::bigint as file_size,
    (ex.export_file_payload -> 'payload' ->> 'exportFileId')::bigint as export_file_id,
    (ex.export_file_payload -> 'payload' ->> 'exportedRows')::bigint as exported_rows,
    (ex.export_file_payload -> 'payload' ->> 'exportFileType')::varchar(256) as export_file_type,
    (ex.export_file_payload -> 'payload' ->> 'organizationId')::bigint as organization_id,
    (ex.export_file_payload -> 'payload' ->> 'operatorExternalUserId')::varchar(256) as operator_external_user_id,
    (ex.export_file_payload ->> 'eventType')::varchar(256) as event_type,
    (ex.export_file_payload ->> 'eventDateTime')::timestamp  as event_date_time,
    (ex.export_file_payload ->> 'eventDescription')::text as event_description,
    (ex.export_file_payload ->> 'traceId')::varchar(256) as trace_id,
    current_timestamp as target_processed_time

    from raw.export_file ex

    {% if is_incremental() %}
        where ex.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
)


select
  export_file_pk,
  event_id,
  file_size,
  export_file_id,
  exported_rows,
  export_file_type,
  organization_id,
  operator_external_user_id,
  event_type,
  event_date_time,
  event_description,
  trace_id,
  -- technical fields
  target_processed_time as processed_time
from source
