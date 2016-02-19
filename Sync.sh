#!/bin/bash

echo "Starting..."
mkdir /tmp/formrx_demo/
cp target/scala-2.11/form-rx-demo-fastopt.js /tmp/formrx_demo/
git checkout gh-pages
if [ $? -eq 0 ]; then
  mv /tmp/formrx_demo/* .
  echo "Syncing gh-pages"
  git commit -am "Sync to gh-pages"
  git push origin gh-pages
  rm -rf /tmp/formrx_demo/
  echo "Done"
else 
  echo "FAILED TO CHECKOUT GH-PAGES!"
fi
