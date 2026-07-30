{{ config(
    materialized = "incremental",
    unique_key = "office_pk",
    incremental_strategy = "merge",
) }}

with base as (
    select
        office_code,
        debt_position_type_org_id,
        CAST(TO_CHAR(date_receipt, 'YYYY') AS INT) AS operating_year,
        office_description,
        md5(coalesce(office_description, '')) as hash_checksum,
        processed_time
    from {{ ref('payment_assessment_detail') }}

    {% if is_incremental() %}
        where processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
),

-- keep only the most recent description per key within the batch
ranked as (
    select
        b.*,
        row_number() over (
            partition by office_code, debt_position_type_org_id, operating_year
            order by processed_time desc
        ) as rn
    from base as b
),

source as (
    select distinct
    {{ dbt_utils.generate_surrogate_key(['office_code', 'debt_position_type_org_id', 'operating_year']) }} as office_pk,
    office_code,
    debt_position_type_org_id,
    operating_year,
    office_description,
    hash_checksum
    from ranked
    where rn = 1
),

new_data as (
    select s.*
    from source s
    left join {{ this }} t on
        s.office_pk = t.office_pk
    where s.hash_checksum <> coalesce(t.hash_checksum, '')
)

select *, current_timestamp as processed_time
from new_data
