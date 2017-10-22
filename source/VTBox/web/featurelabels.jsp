<%-- 
    Document   : genelabels
    Created on : Apr 10, 2017, 2:09:33 PM
    Author     : soumita
--%>

<%@page import="vtbox.SessionUtils"%>
<%@page import="structure.AnalysisContainer"%>
<%@page import="algorithms.clustering.BinaryTree"%>
<%@page import="java.util.HashMap"%>
<%@page import="structure.Data"%>
<%@page import="java.util.ArrayList"%>
<%@page import="structure.CompactSearchResultContainer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    
try {
    
    String analysis_name = request.getParameter("analysis_name");
    AnalysisContainer analysis = (AnalysisContainer)session.getAttribute(analysis_name);

    Data db = analysis.database;
    
    double num_features = db.features.size();
    
    String start_str = request.getParameter("start");
    String end_str = request.getParameter("end");
    
    double start, end;
    if (start_str != null && !start_str.equals("") && !start_str.equals("null")) {
        start = Integer.parseInt(start_str);
    } else {
        start = 0;
    }
    
    if (end_str != null && !end_str.equals("") && !end_str.equals("null")) {
        end = Integer.parseInt(end_str);
    } else {
        end = num_features;
    }
    
    double image_height = 750.0;
    num_features = end - start + 1;
    double feature_height = image_height/num_features;
    
    ArrayList <ArrayList<CompactSearchResultContainer>> search_results = analysis.search_results;
    
    double left_buffer = 10.0;
    double column_width = 20.0;
    double gap = 5.0;
    
    //double image_width = left_buffer + search_results.size()*column_width + (search_results.size()-1)*gap;
    double image_width = 200.0;
    if (analysis.visualizationType == AnalysisContainer.GENE_LEVEL_VISUALIZATION) {
        image_width = 200.0;
    } else if (analysis.visualizationType == AnalysisContainer.PATHWAY_LEVEL_VISUALIZATION) {
        image_width = 350.0;
    }
    
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        
        <svg height="<%=image_height%>" width="<%=image_width%>" style="position: absolute; top: 20px; left: 0px">
        
        <g>
    
    <%
        if (feature_height >= 15.0) {
            
            BinaryTree linkage_tree = analysis.linkage_tree;
            
            for (int i=(int)start; i<=(int)end; i++) {
                String entrez_i = db.features.get(linkage_tree.leaf_ordering.get(i)).entrezId;
                ArrayList <String> genesymbols = db.entrezGeneMap.get(entrez_i);
                String genes = (genesymbols.get(0) + " (" + entrez_i + ")").toUpperCase();
                /*
                for (int j = 0; j < genesymbols.size()-1; j++) {
                    genes += genesymbols.get(j) + ",";
                }
                genes += genesymbols.get(genesymbols.size()-1) + " (" + entrez_i + ")";
                */
                double mid = feature_height*(i - start) + feature_height*0.60;
    %>
    
                <text id="label_<%=i%>" x="0" y="<%=mid%>" font-family="Verdana" font-size="12" fill="black" style="display: inline" ><%=genes%></text>
    
    <%
            }
        }
    %>
    
        </g>
            
        </svg>
        
    </body>
</html>

<%
  
} catch (Exception e) {

    SessionUtils.logException(session, request, e);
    getServletContext().getRequestDispatcher("/Exception.jsp").forward(request, response);
    
}

%>