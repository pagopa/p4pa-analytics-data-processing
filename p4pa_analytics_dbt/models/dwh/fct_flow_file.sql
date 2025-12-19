{{ config(
    materialized = "incremental",
    incremental_strategy = "append",
    )
}}

with base as (
    -- INGESTION
  select
        {{ dbt_utils.generate_surrogate_key(["'INGESTION'",'ingestion_pk']) }} as fct_flow_file_pk,
        event_type,
        ingestion_flow_file_type as flow_type,
        status,
        organization_id,
        total_rows as row_count,
        processed_rows,
        file_size,
        operator_external_user_id,
		  case
        	when operator_external_user_id like 'WS_USER-piattaforma-unitaria%' then 'SYSTEM_USER'
        	when operator_external_user_id like 'WS_USER-%' then 'SIL'
        	else 'OPERATOR'
    	end as operator_type,
        event_date,
        processed_time
  from {{ ref('ingestion') }}
  {% if is_incremental() %}
        where processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
  {% endif %}
  union all
  -- EXPORT
  select
        {{ dbt_utils.generate_surrogate_key(["'EXPORT_FILE'",'export_file_pk']) }} as fct_flow_file_pk,
        event_type,
        export_file_type as flow_type,
        'COMPLETED' as status,
        organization_id,
        exported_rows as row_count,
        exported_rows as processed_rows,
        file_size,
        operator_external_user_id,
		  case
        	when operator_external_user_id like 'WS_USER-piattaforma-unitaria%' then 'SYSTEM_USER'
        	when operator_external_user_id like 'WS_USER-%' then 'SIL'
        	else 'OPERATOR'
    	end as operator_type,
    	event_date,
      processed_time
  from {{ ref('export_file') }}
  {% if is_incremental() %}
        where processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
  {% endif %}
 ),

source as (
    select
    fct_flow_file_pk,
    event_type,
    flow_type,
    status,
    organization_id,
    row_count,
    processed_rows,
    file_size,
    operator_external_user_id,
    operator_type,
    {{ dbt_utils.generate_surrogate_key(['event_date']) }} as event_date_pk,
    processed_time,
    current_timestamp as target_processed_time

    from base as b
)

select
    fct_flow_file_pk,
    event_type,
    flow_type,
    status,
    organization_id,
    row_count,
    processed_rows,
    file_size,
    operator_external_user_id,
    operator_type,
    event_date_pk,
    target_processed_time as processed_time
    from source
