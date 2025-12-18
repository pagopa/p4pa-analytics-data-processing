{{ config(
    materialized = "incremental",
    incremental_strategy = "append"
) }}

-- depends_on = {{ ref('dim_organization') }}

with base as (
  select
    *,
    DATE(receipt_time) as receipt_date
  from {{ ref('auth') }}
),

source as (
  select
    auth_pk,
    event_type,
    source_user_name,
    {{ dbt_utils.generate_surrogate_key(['receipt_date']) }} as receipt_date_pk,
    {{ dbt_utils.generate_surrogate_key(['organization_id']) }} as organization_pk,
    processed_time as src_processed_time,
    current_timestamp as target_processed_time
  from base as b

  {% if is_incremental() %}
    where b.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
  {% endif %}
)

select
  auth_pk as access_pk,
  source_user_name as username,
  receipt_date_pk,
  organization_pk,
  event_type,
  target_processed_time as processed_time
from source
