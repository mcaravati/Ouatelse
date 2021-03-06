package fr.s4e2.ouatelse.managers;

import com.google.common.hash.Hashing;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fr.s4e2.ouatelse.exceptions.DatabaseInitialisationException;
import fr.s4e2.ouatelse.objects.User;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type EntityManagerUser
 */
public class EntityManagerUser {
    private static final String USER_MANAGER_NOT_INITIALIZED = "EntityManagerUser could not be initialized";

    private final ConnectionSource connectionSource;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Dao<User, Long> instance;

    /**
     * Instantiates a new EntityManagerUser
     *
     * @param connectionSource the connection source
     */
    public EntityManagerUser(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        try {
            this.instance = DaoManager.createDao(this.connectionSource, User.class);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, USER_MANAGER_NOT_INITIALIZED);
            throw new DatabaseInitialisationException(USER_MANAGER_NOT_INITIALIZED);
        }
    }

    /**
     * Inserts a user in the database
     *
     * @param user the user to be inserted
     */
    public void create(User user) {
        try {
            this.instance.create(user);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Deletes a user from the database
     *
     * @param user the user to be deleted
     */
    public void delete(User user) {
        try {
            this.instance.delete(user);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Updates a user in the database
     *
     * @param user the user to be updated
     */
    public void update(User user) {
        try {
            this.instance.update(user);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Gets all the users in the database
     *
     * @return na iterator over all the users in the database
     */
    public CloseableIterator<User> getAll() {
        return this.instance.iterator();
    }

    /**
     * Execute a prepared query
     *
     * @param query the prepared query
     * @return the list of results
     */
    public List<User> executeQuery(PreparedQuery<User> query) {
        List<User> results = null;

        try {
            results = this.instance.query(query);
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return results;
    }

    /**
     * Gets all the users
     *
     * @return all the users that are in the database
     */
    public List<User> getQueryForAll() {
        List<User> results = null;

        try {
            results = this.instance.queryForAll();
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return results;
    }

    /**
     * Gets a query builder
     *
     * @return the query builder for the users
     */
    public QueryBuilder<User, Long> getQueryBuilder() {
        return this.instance.queryBuilder();
    }

    /**
     * Gets a corresponding user if exists, else null
     *
     * @param credentials the credentials of the user
     * @param password    the password of the user in plain text
     * @return the user if exists, else null
     */
    public User getUserIfExists(String credentials, String password) {
        User user = null;

        try {
            //noinspection UnstableApiUsage
            user = this.instance.query(this.instance.queryBuilder().where().eq("credentials", credentials)
                    .and().eq("password", Hashing.sha256().hashString(password, StandardCharsets.UTF_8)
                            .toString()).prepare()).stream().findFirst().orElse(null);

        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return user;
    }

    /**
     * Check if an user exists in the database
     *
     * @param user the user to be checked
     * @return true if it exists, else false
     */
    public boolean exists(User user) {
        if (user == null) return false;

        try {
            return this.instance.queryForId(user.getId()) != null;
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }
}
