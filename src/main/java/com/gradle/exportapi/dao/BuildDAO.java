package com.gradle.exportapi.dao;

import static com.gradle.exportapi.dbutil.SQLHelper.*;

import com.gradle.exportapi.model.Build;
import org.knowm.yank.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class BuildDAO {

    static final Logger log = LoggerFactory.getLogger(BuildDAO.class);

    public static long insertBuild(Build build) {

        OffsetDateTime start = OffsetDateTime.ofInstant
                (build.getTimer().getStartTime(), ZoneId.of(build.getTimer().getTimeZoneId()));

        OffsetDateTime finish = OffsetDateTime.ofInstant
                (build.getTimer().getStartTime(), ZoneId.of(build.getTimer().getTimeZoneId()));

        Object[] params = new Object[] {
                build.getBuildId(),
                build.getUserName(),
                build.getRootProjectName(),
                start,
                finish,
                build.getStatus(),
                build.getTagsAsSingleString()
        };


        // If build id exists in the builds table, return the id directly
        String getIdSql = "SELECT id FROM builds WHERE build_id = ?";
        Long id = Yank.queryScalar(getIdSql, Long.class, new Object[] {build.getBuildId()});
        if (id != null) {
            return id;
        }

        String SQL = insert("builds (build_id, user_name, root_project_name, start, finish, status, tags)", params);
        Long generatedId= Yank.insert(SQL, params);
        if(generatedId == 0) {
            throw new RuntimeException("Unable to save build record for " + build.getBuildId());
        }
        return generatedId;
    }

    public static String findLastBuildId() {
        String sql = "select build_id from builds where id in (select max(id) from builds);";
        Build build = Yank.queryBean(sql, Build.class, new Object[0] );
        return build !=null ? build.getBuildId() : null;
    }
}
