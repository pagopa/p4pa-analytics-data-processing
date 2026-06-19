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

# Copy the dbt core project to the workspace (the FS container is read-only except /tmp)
cp -r ${CORE_DBT_PROJECT}/* "${DBT_WORKSPACE}/"

# --- Enterprise Model Management ---
if [ -n "${DBT_GIT_EXTERNAL_REPO}" ]; then
  echo "DBT_GIT_EXTERNAL_REPO is set. Enabling enterprise models ..."
  
  ENTERPRISE_REPO_DIR="/tmp/dbt/target"

  if [ -z "${DBT_GIT_USER_NAME}" ] || [ -z "${DBT_ENV_SECRET_GIT_CREDENTIAL}" ]; then
    echo "ERROR: Git credentials (DBT_GIT_USER_NAME or DBT_ENV_SECRET_GIT_CREDENTIAL) are not set or empty."
    exit 1
  fi

  REPO_URL="https://${DBT_GIT_USER_NAME}:${DBT_ENV_SECRET_GIT_CREDENTIAL}@${DBT_GIT_EXTERNAL_REPO}"
  
  echo "Cloning enterprise repository..."
  rm -rf "${ENTERPRISE_REPO_DIR}"
  git clone -q "${REPO_URL}" "${ENTERPRISE_REPO_DIR}"
  
  if [ -n "${DBT_GIT_EXTERNAL_REPO_REVISION}" ]; then
    echo "Checking out revision ${DBT_GIT_EXTERNAL_REPO_REVISION}..."
    git -C "${ENTERPRISE_REPO_DIR}" checkout -q "${DBT_GIT_EXTERNAL_REPO_REVISION}"
  fi

  echo "" >> "${DBT_WORKSPACE}/packages.yml"
  
  for d in "${ENTERPRISE_REPO_DIR}"/*/; do
    if [ -d "$d" ]; then
      DIR_PATH=$(echo "$d" | sed 's:/*$::')
      echo "  - local: \"${DIR_PATH}\"" >> "${DBT_WORKSPACE}/packages.yml"
    fi
  done
else
  echo "DBT_GIT_EXTERNAL_REPO is not set. Running in core-only mode."
fi

# --- Dbt execution ---
cd "${DBT_WORKSPACE}"

echo "Execute dbt deps ..."
/app/.venv/bin/dbt deps

echo "Execute dbt run ..."
/app/.venv/bin/dbt run --profiles-dir "${DBT_WORKSPACE}" --target-path "${TARGET_DIR}" --log-path "${LOG_DIR}"