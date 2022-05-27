package com.github.andylke.demo.randomuser;

import java.net.InetAddress;

import org.springframework.batch.item.ItemProcessor;

import com.github.andylke.demo.user.User;

public class RandomUserToUserProcessor implements ItemProcessor<RandomUser, User> {

  @Override
  public User process(RandomUser item) throws Exception {
    final User user = new User();

    user.setUsername(item.getLogin().getUsername());
    user.setPassword(item.getLogin().getPassword());
    user.setName(
        item.getName().getTitle()
            + " "
            + item.getName().getFirst()
            + " "
            + item.getName().getLast());
    user.setEmail(item.getEmail());
    user.setAddress(
        item.getLocation().getStreet().getNumber()
            + " "
            + item.getLocation().getStreet().getName());
    user.setCity(item.getLocation().getCity());
    user.setCountry(item.getLocation().getCountry());
    user.setPostcode(item.getLocation().getPostcode());
    user.setCoordinates(
        item.getLocation().getCoordinates().getLongitude()
            + " "
            + item.getLocation().getCoordinates().getLatitude());
    user.setTimezone(
        item.getLocation().getTimezone().getOffset()
            + " "
            + item.getLocation().getTimezone().getDescription());
    user.setNationality(item.getNat());

    user.setCreatedBy(InetAddress.getLocalHost().getHostAddress());

    return user;
  }
}
