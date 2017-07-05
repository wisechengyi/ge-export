package com.gradle.exportapi.dao;

import com.gradle.exportapi.dbutil.SQLHelper;
import com.gradle.exportapi.model.Build;
import org.knowm.yank.Yank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class BuildDAO {

    private static final Logger logger = LoggerFactory.getLogger(BuildDAO.class);

    /**
     * Insert the build if the db does not have it, else get the existing one.
     *
     * @param build a build.
     * @return the id of the build in the `builds` table.
     */
    public static long insertOrGetBuild(Build build) {

        OffsetDateTime start = OffsetDateTime.ofInstant
                (build.getTimer().getStartTime(), ZoneId.of(build.getTimer().getTimeZoneId()));

        OffsetDateTime finish = OffsetDateTime.ofInstant
                (build.getTimer().getStartTime(), ZoneId.of(build.getTimer().getTimeZoneId()));

        Object[] params = new Object[]{
                build.getBuildId(),
                build.getUserName(),
                build.getRootProjectName(),
                start,
                finish,
                build.getStatus(),
                build.getTagsAsSingleString()
        };


        // If build id exists in the builds table, return the id directly
        Long id = Yank.queryScalar("SELECT id FROM builds WHERE build_id = ?", Long.class, new Object[]{build.getBuildId()});
        if (id != null) {
            logger.warn(String.format("%s already in db. Skipped. This could entail an incomplete export", build.getBuildId()));
            return id;
        }

        String SQL = SQLHelper.insert("builds (build_id, user_name, root_project_name, start, finish, status, tags)", params);
        Long generatedId = Yank.insert(SQL, params);
        if (generatedId == 0) {
            throw new RuntimeException("Unable to save build record for " + build.getBuildId());
        }
        return generatedId;
    }

    public static String findLastBuildId() {
        String sql = "select build_id from builds where id in (select max(id) from builds);";
        Build build = Yank.queryBean(sql, Build.class, new Object[0]);
        return build != null ? build.getBuildId() : null;
    }
}
