export DBT_GIT_EXTERNAL_REPO_REVISION=feat-dbt-enterprise-setup # TODO: da rimuovere

#!/bin/sh
set -e
set +x

# --- Configuration ---
DBT_WORKSPACE="/tmp/dbt/source"
CORE_DBT_PROJECT="/app/dbt"
TARGET_DIR="/tmp/dbt/target"
LOG_DIR="/tmp/dbt/logs"

echo "Initialising dbt workspace in ${DBT_WORKSPACE}..."
mkdir -p "${DBT_WORKSPACE}"
mkdir -p "${TARGET_DIR}"
mkdir -p "${LOG_DIR}"

# Copia il progetto dbt core nel workspace (il container FS è read-only tranne /tmp)
cp -r ${CORE_DBT_PROJECT}/* "${DBT_WORKSPACE}/"

# --- Gestione Modelli Enterprise ---
if [ -n "${DBT_GIT_EXTERNAL_REPO}" ]; then
  echo "DBT_GIT_EXTERNAL_REPO is set. Enabling enterprise models (Option A)..."
  echo "" >> "${DBT_WORKSPACE}/packages.yml"
  # Appende la dipendenza git al packages.yml del workspace usando Jinja per le env var
  cat <<EOF >> "${DBT_WORKSPACE}/packages.yml"
  - git: "https://${DBT_GIT_USER_NAME}:${DBT_ENV_SECRET_GIT_CREDENTIAL}@${DBT_GIT_EXTERNAL_REPO_REVISION}"
    revision: "${DBT_GIT_EXTERNAL_REPO_REVISION}"
EOF
else
  echo "DBT_GIT_EXTERNAL_REPO is not set. Running in core-only mode."
fi

# --- Esecuzione dbt ---
cd "${DBT_WORKSPACE}"

echo "Running dbt deps..."
dbt /app/.venv/bin/dbt deps

echo "Running dbt with core arguments..."
dbt /app/.venv/bin/dbt run --profiles-dir "${DBT_WORKSPACE}" --target-path "${TARGET_DIR}" --log-path "${LOG_DIR}"

#EXIT_CODE=$?
#echo "dbt run completed with exit code: ${EXIT_CODE}"
#exit ${EXIT_CODE}