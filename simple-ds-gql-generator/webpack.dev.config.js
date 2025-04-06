var path = require('path');
const HtmlWebpackPlugin = require("html-webpack-plugin");
const CopyPlugin = require('copy-webpack-plugin');
const { HotModuleReplacementPlugin } = require("webpack");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const { createProxyMiddleware } = require('http-proxy-middleware');
const webpack = require('webpack');
const { env } = require('process');
require('dotenv').config({ path: './.env' });

module.exports = {
    entry: path.resolve(__dirname, "src/index.tsx"),
    devtool: 'source-map',
    mode: 'development',
    output: {
        filename: "bundle.js",
        path: path.resolve(__dirname, 'target/dist'),
        publicPath: '/'
    },
    devServer: {
        contentBase: [
            path.resolve(__dirname, 'target/dist'),
            path.resolve(__dirname, 'public')
        ],
        historyApiFallback: true,
        hot: true,
        port: 3000,
        before: (app) => {
            app.use(createProxyMiddleware("/*graphql",
                {
                    target: process.env.DS_ENDPOINT,
                    changeOrigin: true,
                    secure: true,
                    pathRewrite: { '/*graphql': '' }
                }));
        },
        watchOptions: {
            poll: true,
            ignored: "/node_modules/"
        }
    },
    plugins: [
        new HotModuleReplacementPlugin(),
        new CleanWebpackPlugin(),
        new webpack.ProvidePlugin({
            process: 'process/browser',
        }),
        new CopyPlugin({
            patterns: [
                {
                    from: 'public',
                    to: ''
                }
            ]
        }),
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, './template/index.html'),
            minify: false
        }),
    ],
    resolve: {
        extensions: ['.ts', '.tsx', '.js', '.json', '.css', '.env']
    },
    module: {
        rules: [{
            // Include ts, tsx, js, and jsx files.
            test: /\.(ts|js)x?$/,
            exclude: /node_modules/,
            loader: 'babel-loader',
        },
        {
            test: /\.(png|jpe?g|gif|svg)$/i,
            type: 'asset/resource',
            generator: {
                filename: 'images/[name][ext]'
            }
        },
        {
            test: /\.css$/,
            use: [
                { loader: "style-loader" },
                { loader: "css-loader" }
            ]
        }
        ]
    }
};