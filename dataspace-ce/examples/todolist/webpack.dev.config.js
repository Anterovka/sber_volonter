const { merge } = require('webpack-merge');
const dotenv = require('dotenv');

const common = require('./webpack.config');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          { loader: "style-loader" },
          { loader: "css-loader" }
        ]
      }
    ]
  },
  devServer: {
    port: 3000,
    open: true,
    hot: true,
    proxy: {
      '/graphql': {
        target: dotenv.config().parsed.DS_ENDPOINT,
        changeOrigin: true,
        secure: true,
        pathRewrite: { '/graphql': '' }
      }
    }
  }
});
