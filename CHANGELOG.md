# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.1.1-SNAPSHOT]
### Changed

- `subplot` now support 3d plots.

## [0.1.0]
### Added
- Add `plotly` to create a plotly object with dataset, traces and layout.
- Add `save-html` to save a plotly object into a html page.
- Add `online-init` and `offline-init` to insert plotly.min.js into a Gorilla-REPL notebook.
- Add `iplot` to show a plotly object in Gorilla-REPL notebook.
- Add `plot` to send a plotly object to https://plot.ly and get a sharable url.
- Add `set-credentials` to save user-name and api-key in local file.
- Add `group-dataset` to split a dataset into map.
- Add `add-scatter` to plot lines and markers.
- Add `set-dataset`, `set-layout`, `update-layout`, `set-configs`, `update-configs` to set or update a plotly object. 
- Add `embed-url`, embed a sharable url in Gorilla-REPL notebook.
- Add `plot-seq`, enable for comprehension to apply a sequence of add-fn. The implementation is a little ugly. A macro is needed.
- Add `add-annotations`, like geom_text in R.
- Add `add-bar`, plot bar charts.
- Add `subplot`, make subplots of existing traces.
- Add `scatter-3d`, show 3d scatters.
- Add `add-surface`.
- Add `add-histogram-2d` and `add-histogram`
- Add `add-mesh3d`, `add-contour`, `add-heatmap`

[0.1.0]: https://github.com/findmyway/plotly-clj/tree/0.1.0
