#!/usr/bin/env bash
# Creates labels, one milestone per roadmap week, and one issue per unchecked task,
# from docs/ROADMAP.md. Safe to re-run: existing labels, milestones and issues are skipped.
# Uses only the GitHub REST API (gh api), no GraphQL.
#
# Usage: scripts/bootstrap-github.sh owner/repo
# Needs: gh (GitHub CLI), logged in with `gh auth login`, and python3.
set -euo pipefail

REPO="${1:?usage: scripts/bootstrap-github.sh owner/repo}"
ROADMAP="$(dirname "$0")/../docs/ROADMAP.md"
echo "Repository: $REPO"

for label in "task:0e8a16" "bug:d73a4a" "capture:5319e7" "synth:1d76db" "render:fbca04" "page:c2e0c6" "math:b60205"; do
  gh api "repos/$REPO/labels" -f name="${label%%:*}" -f color="${label##*:}" >/dev/null 2>&1 || true
done

# title<TAB>number for every milestone, and every existing issue title
milestones="$(gh api "repos/$REPO/milestones?state=all&per_page=100" --jq '.[] | "\(.title)\t\(.number)"')"
existing_issues="$(gh api --paginate "repos/$REPO/issues?state=all&per_page=100" --jq '.[].title')"

milestone_number() {
  awk -F'\t' -v t="$1" '$1 == t { print $2 }' <<<"$milestones"
}

current=""
while IFS= read -r line; do
  if [[ "$line" =~ ^##\ (Week\ .+)$ ]]; then
    current="${BASH_REMATCH[1]}"
    if [[ -z "$(milestone_number "$current")" ]]; then
      number="$(gh api "repos/$REPO/milestones" -f title="$current" --jq '.number')"
      milestones+=$'\n'"$current"$'\t'"$number"
      echo "milestone: $current"
    fi
  elif [[ "$line" =~ ^##\  ]]; then
    current=""   # sections like "Ideas parking lot" get no issues
  elif [[ -n "$current" && "$line" =~ ^-\ \[\ \]\ (.+)$ ]]; then
    title="${BASH_REMATCH[1]}"
    if ! grep -Fxq -- "$title" <<<"$existing_issues"; then
      python3 -c 'import json,sys; print(json.dumps({"title": sys.argv[1], "body": "From docs/ROADMAP.md, " + sys.argv[2] + ".", "milestone": int(sys.argv[3]), "labels": ["task"]}))' \
        "$title" "$current" "$(milestone_number "$current")" |
        gh api "repos/$REPO/issues" --input - >/dev/null
      existing_issues+=$'\n'"$title"
      echo "  issue: $title"
    fi
  fi
done <"$ROADMAP"
echo "Done."
