// ============================================================================
// (C) Copyright Schalk W. Cronje 2014
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
//
// This code is strongly based upon original Java code found at
//
//   http://svn.apache.org/viewvc/commons/proper/vfs/trunk/sandbox
//
// to which the below license applies. Modification and distribution of
// this code is incompliance with the Apache 2.0 license
//
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ============================================================================
package org.ysb33r.groovy.vfsplugin.cloud.s3

import groovy.transform.CompileStatic
import org.apache.commons.vfs2.FileName
import org.apache.commons.vfs2.FileType
import org.apache.commons.vfs2.provider.AbstractFileName
import org.apache.commons.vfs2.provider.UriParser

/**
 * An SMB URI.  Adds a share name to the generic URI.
 */
@CompileStatic
class S3FileName extends AbstractFileName
{

    private static final char[] USERNAME_RESERVED = ':@/'.toCharArray()
    private static final char[] PASSWORD_RESERVED = '@/?'.toCharArray()

    final String bucket
    private final String accessKey
    private final String secretKey

    protected S3FileName(
        final String scheme,
        final String bucket,
        final String userName,
        final String password,
        final String path,
        final FileType type) {

        super(scheme,path,type)
        this.bucket=bucket
        this.accessKey=userName
        this.secretKey=password
    }

    /**
     * Builds the root URI for this file name.
     */
    @Override
    protected void appendRootUri(StringBuilder buffer, final boolean addPassword) {
        buffer.append("${scheme}://" + credentials(addPassword) + bucket)
    }

    /**
     * Factory method for creating name instances.
     */
    @Override
    public FileName createName(final String path, final FileType type) {
        return new S3FileName(scheme, bucket, accessKey, secretKey, path, type)
    }

    /**
     * append the user credentials
     */
    private String credentials(final boolean addPassword) {
        StringBuilder buffer = new StringBuilder()

        if (accessKey?.size()) {
            UriParser.appendEncoded(buffer, accessKey, USERNAME_RESERVED);
            if (secretKey?.size()) {
                buffer.append(':');
                if (addPassword) {
                    UriParser.appendEncoded(buffer, secretKey, PASSWORD_RESERVED);
                } else {
                    buffer.append("***");
                }
            }
            buffer.append('@')
        }

        return buffer.toString()
    }
}

