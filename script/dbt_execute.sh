#!/bin/sh
set -e
set +x

export DBT_GIT_EXTERNAL_REPO_REVISION=feat-dbt-enterprise-setup # TODO: da rimuovere

# --- Configure git for the authentication ---
export GIT_ASKPASS=/app/script/git-askpass.sh
export GIT_TERMINAL_PROMPT=0 

# --- Configuration ---
DBT_WORKSPACE="/tmp/dbt/source"
CORE_DBT_PROJECT="/app/dbt"
TARGET_DIR="/tmp/dbt/target"
LOG_DIR="/tmp/dbt/logs"

echo "Initialising dbt workspace in ${DBT_WORKSPACE}..."
mkdir -p "${DBT_WORKSPACE}"
mkdir -p "${TARGET_DIR}"
mkdir -p "${LOG_DIR}"

# Copy the dbt core project to the workspace (the FS container is read-only except /tmp)
cp -r ${CORE_DBT_PROJECT}/* "${DBT_WORKSPACE}/"

# --- Enterprise Model Management ---
if [ -n "${DBT_GIT_EXTERNAL_REPO}" ]; then
  echo "DBT_GIT_EXTERNAL_REPO is set. Enabling enterprise models ..."
  echo "" >> "${DBT_WORKSPACE}/packages.yml"
  # Append git dependency to workspace packages.yml using Jinja for env vars
  cat <<EOF >> "${DBT_WORKSPACE}/packages.yml"
  - git: https://${DBT_GIT_EXTERNAL_REPO}.git
    revision: ${DBT_GIT_EXTERNAL_REPO_REVISION}
EOF
else
  echo "DBT_GIT_EXTERNAL_REPO is not set. Running in core-only mode."
fi

# --- Dbt execution ---
cd "${DBT_WORKSPACE}"

echo "Execute dbt deps ..."
/app/.venv/bin/dbt deps

echo "Execute dbt run ..."
/app/.venv/bin/dbt run --profiles-dir "${DBT_WORKSPACE}" --target-path "${TARGET_DIR}" --log-path "${LOG_DIR}"