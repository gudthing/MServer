package gudthing.controllers;

import gudthing.models.Client;
import gudthing.models.ClientDb;
import gudthing.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 30/03/2016.
 */
@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    //  ---------------------------------------GET /------------------------------------------------------
    @RequestMapping(method= RequestMethod.GET)
    public String index(Model model) {

        List<Client> allClients= clientRepository.findAll();

        model.addAttribute("clients", allClients);

        //required to remove thymeleaf error
        if(false){
            WebContext context = new org.thymeleaf.context.WebContext(null,null,null);
            context.setVariable("clients", allClients);
        }
        return "clients";
    }

    //  ---------------------------------------GET /Create-------------------------------------------------------
    @RequestMapping(value = "/create",method = RequestMethod.GET)
    public String addNewClientForm(Model model){
        //required so form can use it on POST
        model.addAttribute("client", new Client());
        return "createClient";
    }

    //  -------------------------------------- POST /Create------------------------------------------------------
    @RequestMapping(value="/create", method= RequestMethod.POST)
    public String processSubmit(@ModelAttribute Client client, Model model){
        model.addAttribute("client", client);
        clientRepository.saveAndFlush(client);
        //good practice to redirect after handling a POST request.
        return "redirect:/clients";
    }

    //   ------------------------------------- GET  /{id}---------------------------------------------------
    @RequestMapping(value="/{clientID}", method=RequestMethod.GET)
    public String getClient(@PathVariable int clientID, Model model){

        Client theClient = clientRepository.findClientByClientID(clientID);

        //required to remove thymeleaf error
        if(false){
            WebContext context = new org.thymeleaf.context.WebContext(null,null,null);
            context.setVariable("theClient", theClient);
        }

        if(theClient == null){
            System.out.println("CLIENT NOT FOUND########################");
            throw new UserNotFoundException(clientID);
        }else{
            model.addAttribute("theClient", theClient);
            return "editClient";
        }
    }

    //   ------------------------------------- POST  /{id}/update---------------------------------------------------
    //Most browsers do not support action=PUT in HTML forms
    @RequestMapping(value="/{clientID}/update", method=RequestMethod.POST)
    public String updateClient(@ModelAttribute Client client, @PathVariable int clientID, Model model){
        if(client == null){
            throw new UserNotFoundException(clientID);
        }else{

            Client editClient = clientRepository.findClientByClientID(clientID);
            editClient.ipAddress = client.ipAddress;
            editClient.portNumber = client.portNumber;
            editClient.description = client.description;
            clientRepository.saveAndFlush(editClient);
            return "redirect:/clients";
        }
    }

    //   ------------------------------------- POST  /{id}/delete---------------------------------------------------
    @RequestMapping(value="/{clientID}/delete", method=RequestMethod.POST)
    public String deleteClient(@ModelAttribute Client client, @PathVariable int clientID, Model model){
        if(client == null){
            throw new UserNotFoundException(clientID);
        }else{

            Client deleteClient = clientRepository.findClientByClientID(clientID);
            clientRepository.delete(deleteClient);
            clientRepository.flush();
            return "redirect:/clients";
        }
    }













//    public String greeting(@RequestParam(value="name", required = false, defaultValue = "world") String name, Model model) {
//        model.addAttribute("name", name);
//    }

    //helper method to get find client by ID
//    private Client getClientById(int clientID){
//        Client theClient = null;
//        for(Client client : allClients){
//            if(client.getClientID() == clientID){
//                theClient = client;
//                break;
//            }
//        }
//        return theClient;
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int clientID) {
        super("Could not find client " + clientID);
    }
}
