---
id: index
title: AI Agent Skills
slug: ai-skills.html
sidebar_label: Introduction
---

Kotest ships an [Agent Skill](https://docs.claude.com/en/docs/claude-code/skills) that teaches AI
coding assistants like Claude Code how to write, migrate, and debug Kotlin tests with Kotest. The skill
covers spec styles, assertions, property-based testing, lifecycle hooks, extensions, multiplatform
setup, and common troubleshooting steps -- the same knowledge in this documentation site, packaged for
an agent to use directly while it edits your code.

The skill source lives in the [`skills/`](https://github.com/kotest/kotest/tree/master/skills) directory
of the main Kotest repository.

## Installing with Claude Code

The skill is distributed as a [plugin](https://docs.claude.com/en/docs/claude-code/plugins) through a
marketplace hosted in the Kotest repository. Add the marketplace once, then install the plugin:

```
/plugin marketplace add kotest/kotest
/plugin install kotest-skills
```

Claude Code will pick up the skill automatically whenever you're working on Kotest-related code -- no
further configuration needed.

## Installing with the skills CLI

Use Vercel's [skills CLI](https://github.com/vercel-labs/skills) to install the skill for supported
agents, including Claude Code, Codex, Cursor, and GitHub Copilot. With Node.js installed, run:

```bash
npx skills add kotest/kotest
```

Follow the prompts to select the agents and installation scope. Using `npx` means you don't need to
install the CLI globally first.

## Installing manually

If you prefer not to use either installer, you can copy the skill
directory directly into any location your tool loads skills from, for example:

```bash
git clone --depth 1 https://github.com/kotest/kotest.git /tmp/kotest-skill-src
cp -r /tmp/kotest-skill-src/skills/kotest .claude/skills/kotest
```

## Keeping up to date

The skill is versioned alongside the rest of the Kotest repository. Re-running
`/plugin marketplace update kotest` (or re-copying the directory for manual installs) will pick up the
latest content.
