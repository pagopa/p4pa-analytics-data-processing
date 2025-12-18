{{ config(
  materialized = "incremental",
  unique_key = "organization_pk",
  incremental_strategy = "merge"
) }}

with base as (
  select
    organization_id,
    organization_name,
    md5(coalesce(organization_name, '')) as hash_checksum,
    row_number() over (partition by (organization_id) order by receipt_time desc) as rn
  from {{ ref('auth') }}

  {% if is_incremental() %}
    where processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
  {% endif %}
),

source as (
  select distinct
    {{ dbt_utils.generate_surrogate_key(['organization_id']) }} as organization_pk,
    coalesce(organization_id, -1) as organization_id,
    coalesce(organization_name, 'none') as organization_name,
    hash_checksum
  from base as b
  where rn = 1
),

new_data as (
  select
    s.*
  from source s
  left join {{ this }} t on
    s.organization_pk = t.organization_pk
  where s.hash_checksum <> coalesce(t.hash_checksum, '')
)

select
  *,
  current_timestamp as processed_time
from new_data
