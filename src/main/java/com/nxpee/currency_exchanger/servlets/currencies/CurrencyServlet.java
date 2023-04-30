package com.nxpee.currency_exchanger.servlets.currencies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxpee.currency_exchanger.dto.CurrenciesDTO;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.service.CurrenciesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(value = "/currency/*", name = "CurrencyServlet")
public class CurrencyServlet extends HttpServlet {

    private final CurrenciesService currenciesService = CurrenciesService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestCode = req.getPathInfo().replace("/", "").toUpperCase();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            PrintWriter writer = resp.getWriter();
            Optional<CurrenciesDTO> currency = currenciesService.findByCode(requestCode);
            if (currency.isPresent()){
                writer.write(objectMapper.writeValueAsString(currency.get()));
            } else {
                resp.sendError(404, "Currency not found");
            }
        }catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        } catch (InvalidParametersException e) {
            resp.sendError(400, e.getMessage());
        }
    }
}
