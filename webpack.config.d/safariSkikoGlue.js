// Optional workaround (build with SKIKO_FORWARDER_FIX=1). Safari 27 and every iPhone browser on iOS 27 (WebKit): while a list is dragged slowly, the app suddenly draws its
// cards at the top left corner and stops reacting where they appear to be. The Kotlin code hands Skia (skiko) the
// right positions, but once JavaScriptCore has optimised the small functions in skiko's JavaScript glue that forward
// each call to Skia's WebAssembly (`f = (...a) => (f = wasm._.f)(...a)`), the numbers reaching Skia arrive as zeros.
// A forwarder that keeps a try/finally block is not optimised that way, and the numbers arrive intact. Chrome,
// Firefox and older Safari behave the same either way.
//
// The rewrite runs on the finished bundle, for both the minified form `f=(...e)=>(f=x._.name)(...e)` and the development form `f = (...a) => (f = loadedWasm._["name"])(...a)`.
// A build where it finds no such forwarders fails, so a skiko update that changes the glue is noticed.
;(function () {
    const forwarder = /([\w$]+)\s*=\s*\(\.\.\.([\w$]+)\)\s*=>\s*\(\s*\1\s*=\s*([\w$]+\._(?:\.[\w$]+|\["[\w$]+"\]))\s*\)\s*\(\s*\.\.\.\2\s*\)/g;

    class SafariSkikoGluePlugin {
        apply(compiler) {
            const { Compilation, sources } = compiler.webpack;
            compiler.hooks.thisCompilation.tap("SafariSkikoGlue", (compilation) => {
                compilation.hooks.processAssets.tap(
                    { name: "SafariSkikoGlue", stage: Compilation.PROCESS_ASSETS_STAGE_OPTIMIZE_INLINE },
                    (assets) => {
                        let rewritten = 0;
                        for (const name of Object.keys(assets)) {
                            if (!name.endsWith(".js")) continue;
                            const source = assets[name].source().toString();
                            const patched = source.replace(forwarder, (match, fn, args, target) => {
                                rewritten++;
                                return `${fn}=(...${args})=>{try{return ${target}(...${args})}finally{}}`;
                            });
                            if (patched !== source) compilation.updateAsset(name, new sources.RawSource(patched));
                        }
                        if (rewritten === 0) {
                            compilation.errors.push(new Error("SafariSkikoGlue: no skiko forwarders found to rewrite"));
                        }
                    },
                );
            });
        }
    }

    if (process.env.SKIKO_FORWARDER_FIX === "1") config.plugins.push(new SafariSkikoGluePlugin());
})();
