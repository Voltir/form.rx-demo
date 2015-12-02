#!/bin/bash

echo "Starting..."
mkdir /tmp/formidable_demo/
cp src/main/resources/* /tmp/formidable_demo/
git checkout gh-pages
mv /tmp/formidable_demo/* .
echo "Syncing gh-pages"
git commit -am "Sync to gh-pages"
git push origin gh-pages
git checkout master
echo "Done"
