## Cutting docs for a new version

Use `./cut-docs.sh` to automate steps 1–4

1. Add the version to [versions.json](./versions.json)
   1. Note: The versions are shown in the listed order, with the first element being the default version.
2. Create the directory `./versioned_docs/version-<RELEASED_VERSION>`
3. Copy the contents of `./docs/` to the folder
4. Copy `./sidebars.js` to `./verioned_sidebars/version-<RELEASED_VERSION>-sidebars.json` and remove `module.exports = ` from the start of the file
5. Push/merge to master to update [kotest.io](https://kotest.io)
6. A crawl of newly added docs will be triggered by [github action _crawl_](/.github/workflows/crawl.yaml)
   * To manually initiate crawling, go to [crawler.algolia.com](https://crawler.algolia.com/admin/crawlers?sort=status&order=ASC&limit=20)
   * Search will be broken until this is triggered

## Updating docs
Make sure to update the versioned docs if you intend to update docs for the current stable version. [/docs](./docs) is for the current snapshot version.

