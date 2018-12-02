#! /bin/bash
openssl aes-256-cbc -K $encrypted_12c8071d2874_key -iv $encrypted_12c8071d2874_iv -in scripts/codesigning.asc.enc -out scripts/codesigning.asc -d
gpg --fast-import scripts/codesigning.asc
