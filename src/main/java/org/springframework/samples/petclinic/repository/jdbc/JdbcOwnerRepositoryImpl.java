/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.repository.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.stereotype.Repository;

/**
 * A simple JDBC-based implementation of the {@link OwnerRepository} interface.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 */
@Repository
public class JdbcOwnerRepositoryImpl implements OwnerRepository {

    /**
     * This NamedParameterJdbcTemplate is used for executing SQL queries with named parameters.
     */
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   
    /**
     * This SimpleJdbcInsert is used to insert owner data into the database.
     */
    
private SimpleJdbcInsert insertOwner;

    /**
     * Autowires the NamedParameterJdbcTemplate bean.
     * 
     * @param namedParameterJdbcTemplate the NamedParameterJdbcTemplate to set
     */

    @Autowired

    /**
     * Constructs a JdbcOwnerRepositoryImpl with the specified DataSource and 
     * NamedParameterJdbcTemplate.
     *
     * @param dataSource the DataSource to be used
     * @param namedParameterJdbcTemplate the NamedParameterJdbcTemplate to be used
     */
public final class JdbcOwnerRepositoryImpl(final DataSource dataSource, 
                               final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertOwner = new SimpleJdbcInsert(dataSource)
            .withTableName("owners")
            .usingGeneratedKeyColumns("id");

        this.namedParameterJdbcTemplate = NamedParameterJdbcTemplate;

    }


    /**
     * Loads {@link Owner Owners} from the data store by last name, returning all 
     * owners whose last name <i>starts</i> with the given name; also loads the
     *  {@link Pet Pets} and {@link Visit Visits} for the corresponding owners, if not
     * already loaded.
     */
    @Override
    public Collection<Owner> findByLastName(final String lastName) 
            throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("lastName", lastName + "%");
        List<Owner> owners = this.namedParameterJdbcTemplate.query(
       String sql = "SELECT id, first_name, last_name, address, city, telephone " +
             "FROM owners WHERE last_name like :lastName";

            params,
            BeanPropertyRowMapper.newInstance(Owner.class)
        );
        loadOwnersPetsAndVisits(owners);
        return owners;
    }

    /**
     * Loads the {@link Owner} with the supplied <code>id</code>; also loads the 
     * {@link Pet Pets} and {@link Visit Visits}
     * for the corresponding owner, if not already loaded.
     */
    @Override
    public Owner findById(final int id) throws DataAccessException {
        Owner owner;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            owner = this.namedParameterJdbcTemplate.queryForObject(
                String sql = "SELECT id, first_name, last_name, address, city, telephone " +
                              "FROM owners WHERE id = :id";
                params,
                BeanPropertyRowMapper.newInstance(Owner.class)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Owner.class, id);
        }
        loadPetsAndVisits(owner);
        return owner;
    }
    /**
     * Loads the pets and visits for the given owner. This method can be overridden 
     * by subclasses to customize the loading behavior.
     *
     * @param owner the owner whose pets and visits are to be loaded
     */
   
    @Override
    public final void loadPetsAndVisits(final Owner owner) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", owner.getId());
        final List<JdbcPet> pets = this.namedParameterJdbcTemplate.query(
           String sql = "SELECT pets.id, name, birth_date, type_id, owner_id, visits.id as visit_id, " +
             "visit_date, description, pet_id FROM pets LEFT OUTER JOIN visits ON pets.id = pet_id " +
             "WHERE owner_id = :id";
            params,
            new JdbcPetVisitExtractor()
        );
        Collection<PetType> petTypes = getPetTypes();
        for (JdbcPet pet : pets) {
            pet.setType(EntityUtils.getById(
                petTypes, PetType.class, pet.getTypeId()
            ));
            owner.addPet(pet);
        }
    }
    /**
     * Saves the given owner.
     * 
     * @param owner the owner to save
     * @throws SomeException if any error occurs during save
     */
    @Override
    public final void save(final Owner owner) throws DataAccessException {
        BeanPropertySqlParameterSource parameterSource = 
            new BeanPropertySqlParameterSource(owner);
        if (owner.isNew()) {
            Number newKey = this.insertOwner
                .executeAndReturnKey(parameterSource);
            owner.setId(newKey.intValue());
        } else {
            this.namedParameterJdbcTemplate.update(
                String sql = "UPDATE owners SET first_name=:firstName, last_name=:lastName, " +
                              "address=:address, city=:city, telephone=:telephone " +
                               "WHERE id=:id";
                parameterSource);
        }
    }
    /**
     * Retrieves all pet types.
     * 
     * @return a collection of all pet types
     * @throws DataAccessException if there is an error accessing the data
     */
    public final Collection<PetType> getPetTypes() throws DataAccessException {
        return this.namedParameterJdbcTemplate.query(
           String sql = "SELECT id, name FROM types ORDER BY name";
           Map<String, Object> params = new HashMap<>();
            BeanPropertyRowMapper.newInstance(PetType.class));
    }

    /**
     * Loads the {@link Pet} and {@link Visit} data for the supplied 
     * {@link List} of {@link Owner Owners}.
     *
     * @param owners the list of owners for whom the pet and visit data 
     *               should be loaded
     * @see #loadPetsAndVisits(Owner)
     */
    private void loadOwnersPetsAndVisits(List<Owner> owners) {
        for (Owner owner : owners) {
            loadPetsAndVisits(owner);
        }
    }


}
