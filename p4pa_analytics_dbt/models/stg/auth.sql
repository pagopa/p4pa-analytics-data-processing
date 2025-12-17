{{ config(
    materialized= "incremental",
    incremental_strategy="append",
  )
}}

WITH source as (
  select
    {{ dbt_utils.generate_surrogate_key(['auth.auth_pk']) }} as auth_pk,
    auth.auth_payload as auth_payload,
    'rt=([^=]*)( +[a-zA-Z]*=)?' as rt_regex,
    'suser=([^=]*)( +[a-zA-Z]*=)?' as suser_regex,
    'traceId=([^=]*)( +[a-zA-Z]*=)?' as trace_id_regex,
    'organizationId=([^=]*)( +[a-zA-Z]*=)?' as org_id_regex,
    'organizationName=([^=]*)( +[a-zA-Z]*=)?' as org_name_regex,
    'grantType=([^=]*)( +[a-zA-Z]*=)?' as grant_type_regex,
    -- technical fields
    current_timestamp as target_processed_time

  from raw.auth auth

  {% if is_incremental() %}
    where auth.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
  {% endif %}
),

normalized_source as (
  select
    s.auth_pk as auth_pk,
    regexp_replace(s.auth_payload,'((?:[^\\])(?:(?:\\\\)*))(\\\|)','\1-pipe-', 'g') as cef_norm,
    regexp_replace(regexp_replace(s.auth_payload, '^([^|]*\|){7}', '', 'g'), '((?:[^\\])(?:(?:\\\\)*))(\\=)','\1-equal-', 'g') as cef_ext_norm,
    s.rt_regex as rt_regex,
    s.suser_regex as suser_regex,
    s.trace_id_regex as trace_id_regex,
    s.org_id_regex as org_id_regex,
    s.org_name_regex as org_name_regex,
    s.grant_type_regex as grant_type_regex,
    -- technical fields
    s.target_processed_time as target_processed_time

  from source s
),

elaborated_source as (
  select
    ns.auth_pk as auth_pk,
    split_part(ns.cef_norm,'|', 1)::varchar(10) as version,
    split_part(ns.cef_norm,'|', 2)::varchar(255) as vendor,
    split_part(ns.cef_norm,'|', 3)::varchar(255) as product,
    split_part(ns.cef_norm,'|', 5)::varchar(255) as event_type,
    replace(replace(split_part(ns.cef_norm,'|', 6), '-pipe-', '|'), '\\', '\') as description,
    substring(ns.cef_ext_norm, rt_regex)::timestamp as rt,
    replace(replace(substring(ns.cef_ext_norm, suser_regex), '-equal-', '='), '\\', '\')::varchar(255) as suser,
    NULLIF(substring(ns.cef_ext_norm, grant_type_regex), '')::varchar(255) as grant_type,
    NULLIF(substring(ns.cef_ext_norm, org_id_regex),'')::bigint as organization_id,
    NULLIF(substring(ns.cef_ext_norm, org_name_regex), '')::varchar(255) as organization_name,
    substring(ns.cef_ext_norm, trace_id_regex)::varchar(255) as trace_id,
    -- technical fields
    ns.target_processed_time as target_processed_time

  from normalized_source ns
)

select
  auth_pk,
  version,
  vendor,
  product,
  event_type,
  description,
  rt as receipt_time,
  trace_id,
  suser as source_user_name,
  grant_type,
  organization_id,
  organization_name,
  -- technical fields
  target_processed_time as processed_time
from elaborated_source es
