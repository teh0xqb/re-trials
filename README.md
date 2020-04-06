# Re-trials

Demo task, using cljs' reagent and cljs-http to consume a REST api and display / edit values.

## Installation

Use the npm or yarn package managers to install:

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

## Release
_TODO
## Contributing
_TODO
### Development
_TODO

## License
[MIT](https://choosealicense.com/licenses/mit/)
