# Re-trials

Demo task, using cljs' reagent and cljs-http to consume a REST api and display / edit values.

## Installation

Use the npm or yarn package managers to install dependencies:

```bash
yarn install
```

See package.json's scripts for all available dev and release tasks.

## Notes

- In general and on a project long-term, I wouldn't use a lot of static inline styles.
That's what `material-ui/withStyles` is for. Is it used occassionally on this demo project.
-  In a pinch, I experimented by adding pre-conditions to some components to simulate PropTypes React and from js world.
- Same as above, left some components with default sizes, which might be strange in some scenarios. Did not want to style further the demo.
- Clean up or re-visit async channels logic.
- On a more mature app, instead of blocking (loading on submit), we could optimistically update the
store locally, and re-fetch data after update. If it did fail, we can rollbar store (persistent structure)
as well as display a material-ui/Snackbar stating if the resource save failed or succeeded.

## Release
TODO

## Contributing
TODO

### Development

TODO

## License
[MIT](https://choosealicense.com/licenses/mit/)
