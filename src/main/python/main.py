import pubchempy as pcp

# a = pcp.Compound.from_cid(5090)
# b = pcp.get_compounds('C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C=C-C', 'smiles')
# b = pcp.get_compounds('phenyl propyl ether', 'name')

b = pcp.get_compounds('CCC |$_AV:1;2;3$|', 'smiles')

# print(a.iupac_name)
print(b[0].cid, b[0].iupac_name, b[0].molecular_formula)
