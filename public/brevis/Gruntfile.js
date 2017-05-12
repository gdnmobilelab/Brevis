module.exports = function(grunt) {
    require('load-grunt-tasks')(grunt); // npm install --save-dev load-grunt-tasks

    let jsFiles = {
        'dist/js/brevis.js': 'src/js/brevis.js',
        'dist/js/brevis-login.js': 'src/js/brevis-login.js',
        'dist/js/brevis-logout.js': 'src/js/brevis-logout.js',
        'dist/js/brevis-service-worker.js': 'src/js/brevis-service-worker.js'
    };

    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        env : {
            dev: {
                NODE_ENV: 'dev',
            },
            stage: {
                NODE_ENV: 'stage'
            },
            prod: {
                NODE_ENV: 'production'
            }
        },
        concurrent: {
            target: {
                tasks: ['watch', 'browserify:devWatch'],
                options: {
                    logConcurrentOutput: true
                }
            }
        },
        watch: {
            scripts: {
                files: ['src/css/*.css'],
                tasks: ['cssmin'],
                options: {
                    interrupt: true,
                },
            },
        },
        copy: {
            main: {
                files: [
                    {expand: true, cwd: 'src/html', src: ['**'], dest: 'dist/html', filter: 'isFile'},
                    {expand: true, cwd: 'src/img', src: ['**'], dest: 'dist/img', filter: 'isFile'},
                    {expand: true, cwd: 'src/manifest', src: ['manifest.json'], dest: 'dist/manifest', filter: 'isFile'},
                ]
            }
        },
        cssmin: {
            options: {
                mergeIntoShorthands: false,
                roundingPrecision: -1
            },
            target: {
                files: {
                    'dist/css/brevis.css': 'src/css/brevis.css'
                }
            }
        },
        uglify: {
            build: {
                files: [{
                    expand: true,
                    cwd: 'dist/js',
                    src: '*.js',
                    dest: 'dist/js'
                }]
            }
        },
        browserify: {
            build: {
                files: jsFiles,
                options: {
                    exclude: ['node_modules/*'],
                    transform: [
                        ['loose-envify'],
                        [
                            'babelify', {
                                presets: ['es2015'],
                                plugins: [
                                    'transform-inline-environment-variables',
                                    'inferno'
                                ]
                            }
                        ]
                    ],
                    plugins: [
                        'bundle-collapser'
                    ]
                }
            },
            devWatch: {
                files: jsFiles,
                options: {
                    watch: true,
                    keepAlive : true,
                    browserifyOptions: {
                        debug: true
                    },
                    exclude: ['node_modules/*'],
                    transform: [
                        ['loose-envify'],
                        [
                            "babelify", {
                                presets: ['es2015'],
                                plugins: [
                                    'transform-inline-environment-variables',
                                    'inferno'
                                ]
                            }
                        ]
                    ],
                    plugins: [
                        'bundle-collapser'
                    ]
                }
            },
            dev: {
                files: jsFiles,
                options: {
                    debug: true,
                    browserifyOptions: {
                        debug: true
                    },
                    exclude: ['node_modules/*'],
                    transform: [[
                        "babelify", {
                            presets: ['es2015'],
                            plugins: [
                                'inferno',
                                'transform-inline-environment-variables'
                            ]
                        }
                    ]],
                    plugins: [
                        'bundle-collapser'
                    ]
                }
            }
        }
    });

    // Todo: make this async
    grunt.registerTask('replace', 'Load constants', function() {
        let assetToken = Date.now();
        let fs = require('fs');

        let jsDir = `${__dirname}/dist/js`;
        let htmlDir = `${__dirname}/dist/html`;
        let js = fs.readdirSync(jsDir);
        let html = fs.readdirSync(htmlDir);

        let readWriteFile = (dir, file) => {
            let filePath = `${dir}/${file}`;
            let fileData = fs.readFileSync(filePath, 'utf8')
                .replace(/__ASSET_TOKEN__/g, assetToken);

            fs.writeFileSync(filePath, fileData, {encoding: 'utf8'});
        };

        html.forEach(readWriteFile.bind(this, htmlDir));
        js.forEach(readWriteFile.bind(this, jsDir));
    });

    // Default task(s).
    grunt.registerTask('default', ['env:dev', 'cssmin', 'copy:main', 'concurrent:target']);
    grunt.registerTask('build:dev', ['env:dev', 'cssmin', 'browserify:dev', 'copy:main']);
    grunt.registerTask('build:stage', ['env:stage', 'cssmin', 'browserify:build', 'copy:main', 'replace', 'uglify:build']);
    grunt.registerTask('build:stageDebug', ['env:stage', 'cssmin', 'browserify:build', 'copy:main', 'replace']);
    grunt.registerTask('build:prod', ['env:prod', 'cssmin', 'browserify:build', 'copy:main', 'replace', 'uglify:build']);
};