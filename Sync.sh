#!/bin/bash

echo "Starting..."
mkdir /tmp/formidable_demo/
cp src/main/resources/* /tmp/formidable_demo/
git checkout gh-pages
if [ $? -eq 0 ]; then
  mv /tmp/formidable_demo/* .
  echo "Syncing gh-pages"
  git commit -am "Sync to gh-pages"
else 
  echo "FAILED TO CHECKOUT GH-PAGES!"
fi
#git push origin gh-pages
#git checkout master
#echo "Done"
