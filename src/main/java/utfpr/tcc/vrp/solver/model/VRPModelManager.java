package utfpr.tcc.vrp.solver.model;

import java.io.File;

import utfpr.tcc.vrp.prop.Path;

public class VRPModelManager {
	public static ModelManager getModel(MathematicalModel model) {
		ModelManager modelManager = null;
		String modelsPath = Path.getInstance().getModelsPath() + model + ".mod";

		
		modelManager = new PathModelManager(model.toString(), modelsPath);
		
		return modelManager;
	}
}
