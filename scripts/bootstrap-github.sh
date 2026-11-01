#!/usr/bin/env bash
# Creates labels, one milestone per roadmap week, and one issue per unchecked task,
# from docs/ROADMAP.md. Safe to re-run: existing milestones and issues are skipped.
#
# Usage: scripts/bootstrap-github.sh [owner/repo]
# Needs: gh (GitHub CLI), logged in with `gh auth login`.
set -euo pipefail

REPO="${1:-$(gh repo view --json nameWithOwner -q .nameWithOwner)}"
ROADMAP="$(dirname "$0")/../docs/ROADMAP.md"
echo "Repository: $REPO"

for label in "task:0e8a16" "bug:d73a4a" "capture:5319e7" "synth:1d76db" "render:fbca04" "page:c2e0c6" "math:b60205"; do
  gh label create "${label%%:*}" --color "${label##*:}" --repo "$REPO" --force >/dev/null
done

existing_milestones="$(gh api "repos/$REPO/milestones?state=all&per_page=100" -q '.[].title')"
existing_issues="$(gh issue list --repo "$REPO" --state all --limit 500 --json title -q '.[].title')"

milestone=""
while IFS= read -r line; do
  if [[ "$line" =~ ^##\ (Week\ .+)$ ]]; then
    milestone="${BASH_REMATCH[1]}"
    if ! grep -Fxq "$milestone" <<<"$existing_milestones"; then
      gh api "repos/$REPO/milestones" -f title="$milestone" >/dev/null
      echo "milestone: $milestone"
    fi
  elif [[ "$line" =~ ^##\  ]]; then
    milestone=""   # sections like "Ideas parking lot" get no issues
  elif [[ -n "$milestone" && "$line" =~ ^-\ \[\ \]\ (.+)$ ]]; then
    title="${BASH_REMATCH[1]}"
    if ! grep -Fxq "$title" <<<"$existing_issues"; then
      gh issue create --repo "$REPO" --title "$title" --milestone "$milestone" --label task \
        --body "From docs/ROADMAP.md, $milestone." >/dev/null
      echo "  issue: $title"
    fi
  fi
done <"$ROADMAP"
echo "Done."
