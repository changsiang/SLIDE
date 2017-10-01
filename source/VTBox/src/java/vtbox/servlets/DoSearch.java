/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vtbox.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import searcher.GeneObject;
import searcher.GoObject;
import searcher.PathwayObject;
import searcher.Searcher;
import structure.AnalysisContainer;
import structure.CompactSearchResultContainer;

/**
 *
 * @author Soumita
 */
public class DoSearch extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            HttpSession session = request.getSession(false);
            
            String analysis_name = request.getParameter("analysis_name");
            String searchString = request.getParameter("searchText");
            String queryType = request.getParameter("queryType");
            String searchType = request.getParameter("searchType");

            AnalysisContainer analysis = (AnalysisContainer)session.getAttribute(analysis_name);
            Searcher searcher = analysis.searcher;

            ArrayList <CompactSearchResultContainer> current_search_results = new ArrayList <CompactSearchResultContainer> ();

            if (queryType != null && queryType.equals("pathid") || queryType.equals("pathname")) {

                StringTokenizer st = new StringTokenizer(searchString, ",");

                while(st.hasMoreTokens()) {
                    ArrayList <PathwayObject> part_paths = searcher.processPathQuery(st.nextToken(), searchType, queryType);
                    for(int i = 0; i < part_paths.size(); i++) {
                        PathwayObject path = part_paths.get(i);
                        for (int j = 0; j < path.entrez_ids.size(); j++) {
                            CompactSearchResultContainer csrc = new CompactSearchResultContainer();
                            csrc.createPathwaySearchResult(path.entrez_ids.get(j), path._id, path.pathway);
                            current_search_results.add(csrc);
                        }
                    }
                }

            } else if (queryType != null && queryType.equals("goid") || queryType.equals("goterm")) {

                StringTokenizer st = new StringTokenizer(searchString, ",");

                while(st.hasMoreTokens()) {
                    ArrayList <GoObject> part_go_terms = searcher.processGOQuery(st.nextToken(), searchType, queryType);
                    for(int i = 0; i < part_go_terms.size(); i++){
                        GoObject go = part_go_terms.get(i);
                        for (int j = 0; j < go.entrez_ids.size(); j++) {
                            CompactSearchResultContainer csrc = new CompactSearchResultContainer();
                            csrc.createGOSearchResult(go.entrez_ids.get(j), go.goID, go.goTerm);
                            current_search_results.add(csrc);
                        }
                    }
                }

            } else if (queryType != null && queryType.equals("entrez") || queryType.equals("genesymbol")) {

                StringTokenizer st = new StringTokenizer(searchString, ",");

                while(st.hasMoreTokens()) {
                    ArrayList <GeneObject> part_genes = searcher.processGeneQuery(st.nextToken(), searchType, queryType);
                    for(int i = 0; i < part_genes.size(); i++){
                        GeneObject gene = part_genes.get(i);
                        CompactSearchResultContainer csrc = new CompactSearchResultContainer();
                        csrc.createGeneSearchResult(gene.entrez_id, gene.genesymbol);
                        current_search_results.add(csrc);
                    }
                }

            }

            ArrayList <ArrayList<CompactSearchResultContainer>> search_results = analysis.search_results;
            search_results.add(current_search_results);

            ArrayList <String> search_strings = analysis.search_strings;
            String eq = "";
            if(searchType.equals("exact")){
                eq = " = ";
            } else {
                eq = " &cong; ";
            }
            String current_search_string = queryType + eq + searchString;
            search_strings.add(current_search_string);
            
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}