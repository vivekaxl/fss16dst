package test.jmetal.JMetalTest;

import java.io.FileNotFoundException;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.front.imp.ArrayFront;

public class NSGAIIRunner extends AbstractAlgorithmRunner {

	public static void main(String[] args) throws JMetalException, FileNotFoundException{
		Problem<DoubleSolution> problem;
		Algorithm<List<DoubleSolution>> algorithm;
		CrossoverOperator<DoubleSolution> crossover;
		MutationOperator<DoubleSolution> mutation;
		SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
		String referenceParetoFront = "";
		
		String problemName;
		if (args.length == 1){
			problemName = args[0];
		} else if (args.length == 2){
			problemName = args[0];
			referenceParetoFront = args[1];
		} else{
			
			problemName = "cloudsim.power.optimization.CloudSimPowerProblemIntDouble";
			referenceParetoFront = "";
		}
		
		problem = ProblemUtils.<DoubleSolution> loadProblem(problemName);	
		
		double crossoverProbability = 0.9;
		double crossoverDistributionIndex = 20.0;
		crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
		
		double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
	    double mutationDistributionIndex = 20.0 ;
	    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;
		
	    selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());
	    
	    algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation)
	            .setSelectionOperator(selection)
	            .setMaxEvaluations(250)
	            .setPopulationSize(100)
	            .build() ;
	    
	    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
	            .execute() ;
	    List<DoubleSolution> population = algorithm.getResult() ;
	    long computingTime = algorithmRunner.getComputingTime() ;
	    
	    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
	    
	    ArrayFront newFront = new ArrayFront(population);
	    JMetalLogger.logger.info("New Pareto front : " + newFront.toString());
	    
	    printFinalSolutionSet(population);
	    if (!referenceParetoFront.equals("")) {
	      printQualityIndicators(population, referenceParetoFront) ;
	    }
	  }
}
