{{ config(
    materialized = "incremental",
    unique_key = "section_pk",
    incremental_strategy = "merge",
) }}

with base as (
    select
        section_code,
        debt_position_type_org_id,
        CAST(TO_CHAR(date_receipt, 'YYYY') AS INT) AS operating_year,
        section_description,
        md5(coalesce(section_description, '')) as hash_checksum,
        processed_time
    from {{ ref('payment_assessment_detail') }}
),

source as (
    select distinct
    {{ dbt_utils.generate_surrogate_key(['section_code', 'debt_position_type_org_id', 'operating_year']) }} as section_pk,
    section_code,
    debt_position_type_org_id,
    operating_year,
    section_description,
    hash_checksum
    from base as b

    {% if is_incremental() %}
        where b.processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
),

new_data as (
    select s.*
    from source s
    left join {{ this }} t on
        s.section_pk = t.section_pk
    where s.hash_checksum <> coalesce(t.hash_checksum, '')
)

select *, current_timestamp as processed_time
from new_data
