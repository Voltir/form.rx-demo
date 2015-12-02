#!/bin/bash

echo "Starting..."
mkdir /tmp/formidable_demo/
cp target/formidable-demo/target/formidable-demo-fastopt.js /tmp/formidable_demo/
git checkout gh-pages
if [ $? -eq 0 ]; then
  mv /tmp/formidable_demo/* .
  echo "Syncing gh-pages"
  git commit -am "Sync to gh-pages"
  git push origin gh-pages
  rm -rf /tmp/formidable_demo/
  git checkout master
  echo "Done"
else 
  echo "FAILED TO CHECKOUT GH-PAGES!"
fi
