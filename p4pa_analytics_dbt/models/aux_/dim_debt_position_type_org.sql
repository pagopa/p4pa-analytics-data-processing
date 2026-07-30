{{ config(
    materialized = "incremental",
    unique_key = "debt_position_type_org_pk",
    incremental_strategy = "merge",
) }}

with source as (
    select
        debt_position_type_org_pk,
        debt_position_type_org_id,
        debt_position_type_id,
        organization_id,
        code,
        description,
        flag_spontaneous,
        processed_time
    from {{ ref("debt_position_type_orgs") }}

    {% if is_incremental() %}
        where processed_time >= (select coalesce(max(processed_time), '1900-01-01') from {{ this }} )
    {% endif %}
),

-- keep only the most recent row on a same key
ranked as (
    select
        s.*,
        row_number() over (partition by debt_position_type_org_id order by processed_time desc) as rn
    from source s
)

select
    debt_position_type_org_pk,
    debt_position_type_org_id,
    debt_position_type_id,
    organization_id,
    code,
    description,
    flag_spontaneous,
    -- technical fields
    current_timestamp as processed_time
from ranked
where rn = 1